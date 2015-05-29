package competition;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import client.MazeComMarshaller;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Replaces communication via TCP by communication via a BlockingQueue.
 * 
 * @author Sebastian Oberhoff
 */
public class QueueMazeComMarshaller implements MazeComMarshaller {
  
  private final BlockingQueue<MazeCom> clientToServerQueue;
  
  @Inject
  public QueueMazeComMarshaller(@Named("clientToServer") BlockingQueue<MazeCom> clientToServerQueue) {
    this.clientToServerQueue = clientToServerQueue;
  }
  
  @Override
  public void marshall(MazeCom mazeCom) {
    try {
      System.out.println("Client queueing: " + mazeCom.getMcType());
      clientToServerQueue.put(mazeCom);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
}
