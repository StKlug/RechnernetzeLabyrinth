package competition;

import static com.google.common.base.Preconditions.checkArgument;
import guiceconfigs.CompetitionManagerConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ai.BoardEvaluator;
import ai.StandardBoardEvaluator;

import com.google.common.base.Preconditions;
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
  
  private final List<T> boardEvaluators = new ArrayList<>();
  
  private final HeadlessServer headlessServer;
  
  private final ClientGroup clientGroup;
  
  /**
   * @param boardEvaluators the collection of boardEvaluators that are supposed to compete against
   * each other. The number of BoardEvaluators must be between 1 and 4.
   */
  public EvaluatorCompetition(Collection<T> boardEvaluators) {
    Preconditions.checkArgument(0 < boardEvaluators.size() && boardEvaluators.size() <= 4,
        "Only 1-4 Evaluators may compete.");
    this.boardEvaluators.addAll(boardEvaluators);
    Injector injector = Guice.createInjector(new CompetitionManagerConfig());
    headlessServer = injector.getInstance(HeadlessServer.class);
    clientGroup = injector.getInstance(ClientGroup.class);
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
   * @param boardEvaluators the new boardEvaluators to replace any previous evaluators. The number
   * of BoardEvaluators must be between 1 and 4.
   */
  public void replaceAllEvaluators(Collection<T> boardEvaluators) {
    checkArgument(0 < boardEvaluators.size() && boardEvaluators.size() <= 4,
        "Only 1-4 Evaluators may compete.");
    this.boardEvaluators.clear();
    this.boardEvaluators.addAll(boardEvaluators);
  }
  
  /**
   * Shuffles the participating evaluators and then runs a single game
   * 
   * @return the BoardEvaluator that won the game
   */
  public T runCompetition() {
    Collections.shuffle(boardEvaluators);
    headlessServer.runGame();
    return clientGroup.runClients(boardEvaluators);
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
