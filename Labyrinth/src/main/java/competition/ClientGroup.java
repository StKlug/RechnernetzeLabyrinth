package competition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

import jaxb.MazeCom;
import ai.Evaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * A group of up to four Clients that can repeatedly participate in games using the given
 * Evaluators.
 *
 * @author Sebastian Oberhoff
 */
public final class ClientGroup {
  private final ImmutableSet<ReconfigurableClient> clients;

  private final ExecutorService executorService;

  @Inject
  public ClientGroup(ExecutorService executorService, Collection<BlockingQueue<MazeCom>> inputQueues, BlockingQueue<MazeCom> outputQueue) {
    this.executorService = executorService;
    Builder<ReconfigurableClient> builder = ImmutableSet.builder();
    for (BlockingQueue<MazeCom> inputQueue : inputQueues) {
      builder.add(new ReconfigurableClient(inputQueue, outputQueue));
    }
    this.clients = builder.build();
  }

  /**
   * Runs one Client for each Evaluator passed in and determines the winner.
   *
   * @return the winning Evaluator
   */
  public <T extends Evaluator> T runClients(Collection<T> evaluators) throws InterruptedException {
    Set<T> winners = new HashSet<>();
    CountDownLatch countDownLatch = new CountDownLatch(evaluators.size());
    Iterator<ReconfigurableClient> iterator = clients.iterator();
    Set<Future<?>> runningClients = new HashSet<>();
    for (T evaluator : evaluators) {
      ReconfigurableClient client = iterator.next();
      client.setDelegate(evaluator);
      runningClients.add(executorService.submit(() -> {
        if (client.play()) {
          winners.add(evaluator);
        }
        countDownLatch.countDown();
      }));
    }
    try {
      countDownLatch.await();
    }catch (InterruptedException e){
      runningClients.forEach(future -> future.cancel(true));
      throw e;
    }
    // defensive programming, there should be one and only one winner
    return Iterables.getOnlyElement(winners);
  }
}
