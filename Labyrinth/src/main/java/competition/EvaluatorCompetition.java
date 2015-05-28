package competition;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.Game;
import util.GuiceConfig;
import ai.BoardEvaluator;
import ai.StandardBoardEvaluator;
import client.Client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is designed to run repeated competitions between {@link BoardEvaluator}'s in order to
 * find superior AIs.
 * 
 * @author Sebastian Oberhoff
 */
public final class EvaluatorCompetition<T extends BoardEvaluator> {
  
  private final List<T> boardEvaluators;
  
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  
  /**
   * @param boardEvaluators the list of boardEvaluators that are supposed to compete against each
   * other. The number of BoardEvaluators must be between 1 and 4.
   */
  public EvaluatorCompetition(List<T> boardEvaluators) {
    Preconditions.checkArgument(0 < boardEvaluators.size() && boardEvaluators.size() <= 4,
        "Only 1-4 Evaluators may compete.");
    this.boardEvaluators = boardEvaluators;
  }
  
  /**
   * Replaces an old BoardEvaluator with a new BoardEvaluator. This method is intended to be used in
   * order to continuously improve the skill of the participating AIs.
   */
  public void replaceEvaluator(T oldEvaluator, T newEvaluator) {
    checkArgument(boardEvaluators.contains(oldEvaluator), "The old evaluator wasn't present");
    boardEvaluators.remove(oldEvaluator);
    boardEvaluators.add(newEvaluator);
  }
  
  /**
   * Runs a single game on a headless server (no UI visible). The server is started and torn down
   * internally. The BoardEvaluators participate in a random order.
   * 
   * @return the winning BoardEvaluator
   */
  public T runCompetition() {
    Game game = startGame();
    T winner = runClients();
    game.stopGame();
    return winner;
  }
  
  /**
   * @return the running game which should be stopped via {@link Game#stopGame()} when no longer
   * running.
   */
  private Game startGame() {
    Game game = new Game();
    game.setUserinterface(new MockUI());
    String playerArg = "-n" + boardEvaluators.size();
    game.parsArgs(new String[] { playerArg });
    executorService.submit(game::run);
    return game;
  }
  
  /**
   * Shuffles the participating BoardEvaluators, runs each of them in a Client using a separate
   * thread and waits for all Clients to finish playing before returning the winner.
   * 
   * @return the winning BoardEvaluator
   */
  private T runClients() {
    Collections.shuffle(boardEvaluators);
    CountDownLatch countDownLatch = new CountDownLatch(boardEvaluators.size());
    Set<T> winners = new HashSet<>();
    for (T boardEvaluator : boardEvaluators) {
      executorService.submit(() -> {
        if (runEvaluator(boardEvaluator)) {
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
  
  /**
   * Launches a single Client with the given BoardEvaluator. This method should be launched in a
   * separate Thread to avoid deadlock.
   * 
   * @return true if the BoardEvaluator won
   */
  private static boolean runEvaluator(BoardEvaluator boardEvaluator) {
    Injector injector = Guice.createInjector(new GuiceConfig(boardEvaluator));
    return injector.getInstance(Client.class).play();
  }
  
  public static void main(String[] args) throws Exception {
    List<StandardBoardEvaluator> boardEvaluators = Lists.newArrayList(
        new StandardBoardEvaluator(),
        new StandardBoardEvaluator(),
        new StandardBoardEvaluator(),
        new StandardBoardEvaluator());
    EvaluatorCompetition<StandardBoardEvaluator> clientGroup =
        new EvaluatorCompetition<>(boardEvaluators);
    for (int i = 0; i < 1000; i++) {
      // TODO: Do something more productive than just printing out the winner
      System.err.println("The winner is: " + clientGroup.runCompetition());
    }
  }
}
