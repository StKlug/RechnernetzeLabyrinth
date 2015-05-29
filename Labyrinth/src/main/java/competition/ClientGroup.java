package competition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import jaxb.MazeCom;
import ai.BoardEvaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ClientGroup {
  
  private final ImmutableSet<ReconfigurableClient> clients;
  
  private final ExecutorService executorService;
  
  @Inject
  public ClientGroup(ExecutorService executorService,
      Collection<BlockingQueue<MazeCom>> inputQueues,
      BlockingQueue<MazeCom> outputQueue) {
    this.executorService = executorService;
    Builder<ReconfigurableClient> builder = ImmutableSet.builder();
    for (BlockingQueue<MazeCom> inputQueue : inputQueues) {
      builder.add(new ReconfigurableClient(inputQueue, outputQueue));
    }
    this.clients = builder.build();
  }
  
  public <T extends BoardEvaluator> T runClients(Collection<T> boardEvaluators) {
    Set<T> winners = new HashSet<>();
    CountDownLatch countDownLatch = new CountDownLatch(boardEvaluators.size());
    Iterator<ReconfigurableClient> iterator = clients.iterator();
    for (T boardEvaluator : boardEvaluators) {
      ReconfigurableClient client = iterator.next();
      client.setDelegate(boardEvaluator);
      executorService.submit(() -> {
        if (client.play()) {
          winners.add(boardEvaluator);
        }
        countDownLatch.countDown();
      });
    }
    try {
      countDownLatch.await();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return Iterables.getOnlyElement(winners);
  }
}
