package competition.featureevaluator;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import util.Loggers;
import ai.featureevaluator.DistanceToTreasure;
import ai.featureevaluator.Feature;
import ai.featureevaluator.IsStandingOnTreasure;

import competition.EvaluatorCompetition;

public class RunEvolution {
  
  public static void main(String[] args) {
    EvaluatorCompetition<EvolvableFeatureEvaluator> competition =
        new EvaluatorCompetition<>(createNextGeneration(createFirstEvaluator()));
    for (int i = 1; i <= 1000; i++) {
      EvolvableFeatureEvaluator winner = competition.runCompetition();
      Loggers.EVOLUTION.info("Winner: " + winner);
      Set<EvolvableFeatureEvaluator> nextGeneration = createNextGeneration(winner);
      competition.replaceAllEvaluators(nextGeneration);
    }
  }
  
  private static Set<EvolvableFeatureEvaluator> createNextGeneration(
      EvolvableFeatureEvaluator parent) {
    Set<EvolvableFeatureEvaluator> children = new HashSet<>();
    for (int j = 1; j <= 4; j++) {
      EvolvableFeatureEvaluator child = parent.copy();
      child.evolve(5);
      children.add(child);
    }
    Loggers.EVOLUTION.info("Next generation: "
        + children.stream().map(Object::toString).reduce(String::concat).orElse(""));
    return children;
  }
  
  private static EvolvableFeatureEvaluator createFirstEvaluator() {
    Map<Feature, Integer> weightedSubFeatures = new LinkedHashMap<>();
    weightedSubFeatures.put(new IsStandingOnTreasure(), 50);
    weightedSubFeatures.put(new DistanceToTreasure(), -50);
    return new EvolvableFeatureEvaluator(weightedSubFeatures);
  }
}
