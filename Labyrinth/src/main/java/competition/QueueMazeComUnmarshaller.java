package competition;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import client.MazeComUnmarshaller;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Replaces communication via TCP by communication via a BlockingQueue.
 * 
 * @author Sebastian Oberhoff
 */
public class QueueMazeComUnmarshaller implements MazeComUnmarshaller {
  
  private final BlockingQueue<MazeCom> serverToClientQueue;
  
  @Inject
  public QueueMazeComUnmarshaller(
      @Named("serverToClient") BlockingQueue<MazeCom> serverToClientQueue) {
    this.serverToClientQueue = serverToClientQueue;
  }
  
  @Override
  public MazeCom unmarshall() {
    try {
      MazeCom mazeCom = serverToClientQueue.take();
      System.out.println("Client unqueueing:" + mazeCom.getMcType());
      return mazeCom;
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  
}
