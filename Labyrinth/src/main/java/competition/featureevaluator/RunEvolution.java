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

public class RunEvolution
{
    public static void main(String[] args)
    {
        EvaluatorCompetition<EvolvableFeatureEvaluator> competition = new EvaluatorCompetition<>(createNextGeneration(createFirstEvaluator()));
        singleElimination(competition);
    }

    private static void multipleElimination(EvaluatorCompetition<EvolvableFeatureEvaluator> competition)
    {
        System.out.println(competition.runCompetition(100));
    }

    private static void singleElimination(EvaluatorCompetition<EvolvableFeatureEvaluator> competition)
    {
        for (int i = 1; i <= 1000; i++)
        {
            EvolvableFeatureEvaluator winner = competition.runCompetition();
            Loggers.EVOLUTION.info("Winner: " + winner);
            Set<EvolvableFeatureEvaluator> nextGeneration = createNextGeneration(winner);
            competition.replaceAllEvaluators(nextGeneration);
        }
    }

    private static Set<EvolvableFeatureEvaluator> createNextGeneration(EvolvableFeatureEvaluator winner)
    {
        Set<EvolvableFeatureEvaluator> nextGeneration = new HashSet<>();
        nextGeneration.add(winner);
        for (int j = 1; j <= 3; j++)
        {
            EvolvableFeatureEvaluator newCompetitor = winner.copy();
            newCompetitor.evolve(5);
            nextGeneration.add(newCompetitor);
        }
        Loggers.EVOLUTION.info("Next generation: " + nextGeneration.stream().map(Object::toString).reduce(String::concat).orElse(""));
        return nextGeneration;
    }

    private static EvolvableFeatureEvaluator createFirstEvaluator()
    {
        Map<Feature, Integer> weightedSubFeatures = new LinkedHashMap<>();
        weightedSubFeatures.put(new IsStandingOnTreasure(), 50);
        weightedSubFeatures.put(new DistanceToTreasure(), -50);
        return new EvolvableFeatureEvaluator(weightedSubFeatures);
    }
}
