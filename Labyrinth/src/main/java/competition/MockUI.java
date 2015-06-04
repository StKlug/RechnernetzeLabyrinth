package competition;

import java.util.List;

import jaxb.MoveMessageType;
import server.Board;
import server.Game;
import server.Player;
import server.userInterface.UI;

/**
 * UI that does nothing. Used to run the server in headless mode (no UI visible).
 * 
 * @author Sebastian Oberhoff
 */
public final class MockUI implements UI
{
    @Override
    public void displayMove(MoveMessageType mm, Board b, long moveDelay, long shiftDelay)
    {
    }

    @Override
    public void updatePlayerStatistics(List<Player> stats, Integer current)
    {
    }

    @Override
    public void init(Board b)
    {
    }

    @Override
    public void setGame(Game g)
    {
    }

    @Override
    public void gameEnded(Player winner)
    {
    }
}
