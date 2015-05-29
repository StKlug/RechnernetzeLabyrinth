package competition;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import client.MazeComUnmarshaller;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DirectMazeComUnmarshaller implements MazeComUnmarshaller {
  
  private final BlockingQueue<MazeCom> inputQueue;
  
  @Inject
  public DirectMazeComUnmarshaller(@Named("input") BlockingQueue<MazeCom> inputQueue) {
    this.inputQueue = inputQueue;
  }
  
  @Override
  public MazeCom unmarshall() {
    try {
      MazeCom mazeCom = inputQueue.take();
      System.out.println("Client unqueueing:" + mazeCom.getMcType());
      return mazeCom;
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  
}
