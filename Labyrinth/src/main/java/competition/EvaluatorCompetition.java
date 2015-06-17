package competition;

import static com.google.common.base.Preconditions.checkArgument;

import guiceconfigs.EvaluatorCompetitionConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ai.Evaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is designed to run repeated competitions between {@link Evaluator}'s in order to find
 * superior AIs.
 *
 * @author Sebastian Oberhoff
 */
public final class EvaluatorCompetition<T extends Evaluator> {

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  private final List<T> evaluators = new ArrayList<>();

  private final HeadlessServer headlessServer;

  private final ClientGroup clientGroup;

  /**
   * @param evaluators the collection of Evaluators that are supposed to compete against each other.
   *                   The number of Evaluators must be between 1 and 4.
   */
  public EvaluatorCompetition(Collection<T> evaluators) {
    Preconditions.checkArgument(0 < evaluators.size() && evaluators.size() <= 4, "Only 1-4 Evaluators may compete.");
    this.evaluators.addAll(evaluators);
    Injector injector = Guice.createInjector(new EvaluatorCompetitionConfig());
    headlessServer = injector.getInstance(HeadlessServer.class);
    clientGroup = injector.getInstance(ClientGroup.class);
  }

  /**
   * Replaces an old Evaluator with a new Evaluator. This method is intended to be used in order to
   * continuously improve the skill of the participating AIs.
   */
  public void replaceEvaluator(T oldEvaluator, T newEvaluator) {
    checkArgument(evaluators.contains(oldEvaluator), "The old evaluator wasn't present");
    evaluators.remove(oldEvaluator);
    evaluators.add(newEvaluator);
  }

  /**
   * @param evaluators the new Evaluators to replace any previous evaluators. The number of
   *                   Evaluators must be between 1 and 4.
   */
  public void replaceAllEvaluators(Collection<T> evaluators) {
    checkArgument(0 < evaluators.size() && evaluators.size() <= 4, "Only 1-4 Evaluators may compete.");
    this.evaluators.clear();
    this.evaluators.addAll(evaluators);
  }

  /**
   * Shuffles the participating evaluators and then runs a single game
   *
   * @return the Evaluator that won the game or null if the thread executing this method was interrupted
   */
  public T runCompetition() {
    Collections.shuffle(evaluators);
    Future<?> game = headlessServer.runGame();
    try {
      return clientGroup.runClients(evaluators);
    } catch (InterruptedException e) {
      game.cancel(true);
      return null;
    }
  }

  /**
   * Runs the {@link #runCompetition} method in a separate thread.
   *
   * @return the Future describing the ongoing game. If the game takes too long,
   * consider calling {@link Future#cancel}(true) before starting another game.
   */
  public Future<T> runCompetitionInNewThread() {
    return executorService.submit((Callable<T>) this::runCompetition);
  }

  /**
   * Runs the current participants for multiple games, shuffling starting positions before each
   * game.
   *
   * @return a map from participating {@link Evaluator}s to the number of games they won
   */
  public ImmutableMultimap<T, Integer> runCompetition(int numberOfRounds) {
    Map<T, Integer> scores = new HashMap<>();
    for (int i = 1; i <= numberOfRounds; i++) {
      T winner = runCompetition();
      int previousScore = Optional.ofNullable(scores.get(winner)).orElse(0);
      scores.put(winner, ++previousScore);
    }
    Builder<T, Integer> builder = ImmutableMultimap.builder();
    scores.entrySet().forEach(builder::put);
    return builder.build();
  }
}
