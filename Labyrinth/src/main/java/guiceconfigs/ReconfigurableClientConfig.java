package guiceconfigs;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import ai.Evaluator;
import client.MazeComMarshaller;
import client.MazeComUnmarshaller;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import competition.QueueMazeComMarshaller;
import competition.QueueMazeComUnmarshaller;

/**
 * @author Sebastian Oberhoff
 */
public class ReconfigurableClientConfig extends AbstractModule {
  
  private final Evaluator evaluator;
  
  private final BlockingQueue<MazeCom> serverToClientQueue;
  
  private final BlockingQueue<MazeCom> clientToServerQueue;
  
  public ReconfigurableClientConfig(
      Evaluator evaluator,
      BlockingQueue<MazeCom> serverToClientQueue,
      BlockingQueue<MazeCom> clientToServerQueue) {
    this.evaluator = evaluator;
    this.serverToClientQueue = serverToClientQueue;
    this.clientToServerQueue = clientToServerQueue;
  }
  
  @Override
  protected void configure() {
    bind(Evaluator.class).toInstance(evaluator);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("serverToClient"))
        .toInstance(serverToClientQueue);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("clientToServer"))
        .toInstance(clientToServerQueue);
    bind(MazeComUnmarshaller.class).to(QueueMazeComUnmarshaller.class);
    bind(MazeComMarshaller.class).to(QueueMazeComMarshaller.class);
  }
}
