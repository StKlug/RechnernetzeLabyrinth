package competition;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import jaxb.MazeCom;
import jaxb.MazeComType;
import networking.Connection;
import server.Game;
import server.Player;
import util.Loggers;

/**
 * This class overrides the behavior of the {@link Connection} class and facilitates communication
 * via BlockingQueues rather than TCP sockets.
 * 
 * @author Sebastian Oberhoff
 */
public final class QueueConnection extends Connection
{
    private final BlockingQueue<MazeCom> inputQueue;

    private final BlockingQueue<MazeCom> outputQueue;

    private final CountDownLatch initialized = new CountDownLatch(1);

    public QueueConnection(Game g, int newId, BlockingQueue<MazeCom> inputQueue, BlockingQueue<MazeCom> outputQueue)
    {
        super(new MockSocket(), g, newId);
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void sendMessage(MazeCom mc, boolean withTimer)
    {
        try
        {
            Loggers.COMPETITION.debug("Server queueing: " + mc.getMcType());
            outputQueue.put(mc);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public MazeCom receiveMessage()
    {
        try
        {
            MazeCom mazeCom = inputQueue.take();
            Loggers.COMPETITION.debug("Server unqueueing: " + mazeCom.getMcType());
            // Logins get unqueued by the class LoginThread. This block communicates to the HeadlessServer
            // thread that the client has responded.
            if (mazeCom.getMcType() == MazeComType.LOGIN)
            {
                initialized.countDown();
            }
            return mazeCom;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player login(int newId)
    {
        Player player = super.login(newId);
        try
        {
            // wait for the LoginThread launched behind super.login to receive the login
            initialized.await();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return player;
    }
}
