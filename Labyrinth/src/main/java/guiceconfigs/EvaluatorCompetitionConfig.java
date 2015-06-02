package guiceconfigs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import jaxb.MazeCom;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * @author Sebastian Oberhoff
 */
public final class EvaluatorCompetitionConfig extends AbstractModule {
  
  @Override
  public void configure() {
    bind(Executor.class).toInstance(Executors.newCachedThreadPool());
    bind(new TypeLiteral<BlockingQueue<MazeCom>>() {}).toInstance(new LinkedBlockingQueue<>());
    bind(new TypeLiteral<Collection<BlockingQueue<MazeCom>>>() {})
        .toInstance(createServerToClientQueues());
  }
  
  private Collection<BlockingQueue<MazeCom>> createServerToClientQueues() {
    Collection<BlockingQueue<MazeCom>> serverToClientQueues = new ArrayList<>();
    for (int numberOfPlayers = 1; numberOfPlayers <= 4; numberOfPlayers++) {
      serverToClientQueues.add(new LinkedBlockingQueue<>());
    }
    return serverToClientQueues;
  }
}
