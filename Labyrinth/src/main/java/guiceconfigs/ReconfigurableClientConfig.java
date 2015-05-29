package guiceconfigs;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import ai.BoardEvaluator;
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
  
  private final BoardEvaluator boardEvaluator;
  
  private final BlockingQueue<MazeCom> serverToClientQueue;
  
  private final BlockingQueue<MazeCom> clientToServerQueue;
  
  public ReconfigurableClientConfig(
      BoardEvaluator boardEvaluator,
      BlockingQueue<MazeCom> serverToClientQueue,
      BlockingQueue<MazeCom> clientToServerQueue) {
    this.boardEvaluator = boardEvaluator;
    this.serverToClientQueue = serverToClientQueue;
    this.clientToServerQueue = clientToServerQueue;
  }
  
  @Override
  protected void configure() {
    bind(BoardEvaluator.class).toInstance(boardEvaluator);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("serverToClient"))
        .toInstance(serverToClientQueue);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("clientToServer"))
        .toInstance(clientToServerQueue);
    bind(MazeComUnmarshaller.class).to(QueueMazeComUnmarshaller.class);
    bind(MazeComMarshaller.class).to(QueueMazeComMarshaller.class);
  }
}
