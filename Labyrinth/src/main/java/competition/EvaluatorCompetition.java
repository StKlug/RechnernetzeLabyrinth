package competition;

import static com.google.common.base.Preconditions.checkArgument;
import guiceconfigs.CompetitionManagerConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ai.Evaluator;
import ai.linearevaluator.LinearEvaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is designed to run repeated competitions between {@link Evaluator}'s in order to find
 * superior AIs.
 * 
 * @author Sebastian Oberhoff
 */
public final class EvaluatorCompetition<T extends Evaluator> {
  
  private final List<T> evaluators = new ArrayList<>();
  
  private final HeadlessServer headlessServer;
  
  private final ClientGroup clientGroup;
  
  /**
   * @param evaluators the collection of Evaluators that are supposed to compete against each
   * other. The number of Evaluators must be between 1 and 4.
   */
  public EvaluatorCompetition(Collection<T> evaluators) {
    Preconditions.checkArgument(0 < evaluators.size() && evaluators.size() <= 4,
        "Only 1-4 Evaluators may compete.");
    this.evaluators.addAll(evaluators);
    Injector injector = Guice.createInjector(new CompetitionManagerConfig());
    headlessServer = injector.getInstance(HeadlessServer.class);
    clientGroup = injector.getInstance(ClientGroup.class);
  }
  
  /**
   * Replaces an old Evaluator with a new Evaluator. This method is intended to be used in
   * order to continuously improve the skill of the participating AIs.
   */
  public void replaceEvaluator(T oldEvaluator, T newEvaluator) {
    checkArgument(evaluators.contains(oldEvaluator), "The old evaluator wasn't present");
    evaluators.remove(oldEvaluator);
    evaluators.add(newEvaluator);
  }
  
  /**
   * @param valuators the new Evaluators to replace any previous evaluators. The number
   * of Evaluators must be between 1 and 4.
   */
  public void replaceAllEvaluators(Collection<T> evaluators) {
    checkArgument(0 < evaluators.size() && evaluators.size() <= 4,
        "Only 1-4 Evaluators may compete.");
    this.evaluators.clear();
    this.evaluators.addAll(evaluators);
  }
  
  /**
   * Shuffles the participating evaluators and then runs a single game
   * 
   * @return the Evaluator that won the game
   */
  public T runCompetition() {
    Collections.shuffle(evaluators);
    headlessServer.runGame();
    return clientGroup.runClients(evaluators);
  }
  
  public static void main(String[] args) throws Exception {
    BasicConfigurator.configure();
    Logger.getRootLogger().setLevel(Level.INFO);
    
    List<LinearEvaluator> evaluators = Lists.newArrayList(
        new LinearEvaluator(),
        new LinearEvaluator(),
        new LinearEvaluator(),
        new LinearEvaluator());
    EvaluatorCompetition<LinearEvaluator> clientGroup =
        new EvaluatorCompetition<>(evaluators);
    for (int i = 0; i < 1000; i++) {
      // TODO: Do something more productive than just printing out the winner
      System.err.println("The winner is: " + clientGroup.runCompetition());
    }
  }
}
