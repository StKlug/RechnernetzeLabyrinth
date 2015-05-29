package guiceconfigs;

import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import ai.BoardEvaluator;
import client.MazeComMarshaller;
import client.MazeComUnmarshaller;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import competition.DirectMazeComMarshaller;
import competition.DirectMazeComUnmarshaller;

public class ReconfigurableClientConfig extends AbstractModule {
  
  private final BoardEvaluator boardEvaluator;
  
  private final BlockingQueue<MazeCom> inputQueue;
  
  private final BlockingQueue<MazeCom> outputQueue;
  
  public ReconfigurableClientConfig(
      BoardEvaluator boardEvaluator,
      BlockingQueue<MazeCom> inputQueue,
      BlockingQueue<MazeCom> outputQueue) {
    this.boardEvaluator = boardEvaluator;
    this.inputQueue = inputQueue;
    this.outputQueue = outputQueue;
  }
  
  @Override
  protected void configure() {
    bind(BoardEvaluator.class).toInstance(boardEvaluator);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("input"))
        .toInstance(inputQueue);
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).annotatedWith(Names.named("output"))
    .toInstance(outputQueue);
    bind(MazeComUnmarshaller.class).to(DirectMazeComUnmarshaller.class);
    bind(MazeComMarshaller.class).to(DirectMazeComMarshaller.class);
  }
}
