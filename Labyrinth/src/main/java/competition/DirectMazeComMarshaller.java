package competition;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import client.MazeComMarshaller;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DirectMazeComMarshaller implements MazeComMarshaller {
  
  private final BlockingQueue<MazeCom> outputQueue;
  
  @Inject
  public DirectMazeComMarshaller(@Named("output") BlockingQueue<MazeCom> outputQueue) {
    this.outputQueue = outputQueue;
  }
  
  @Override
  public void marshall(MazeCom mazeCom) {
    try {
      System.out.println("Client queueing: " + mazeCom.getMcType());
      outputQueue.put(mazeCom);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
}
