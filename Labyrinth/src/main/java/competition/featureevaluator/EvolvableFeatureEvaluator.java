package competition.featureevaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;
import ai.Evaluator;
import ai.featureevaluator.Feature;
import ai.featureevaluator.SingleFeatureEvaluator;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of the {@link Evaluator} interface using a linear combination of sub features
 * (each feature has a weight and the final measure of a board is the weighted sum of the sub
 * features).
 * <p>
 * Furthermore this class allows gradual evolutionary improvement by combining the {@link #copy} and
 * {@link #evolve} methods.
 * 
 * @author Sebastian Oberhoff
 */
public final class EvolvableFeatureEvaluator implements Evaluator {
  
  private static final Random random = new Random();
  
  private final Map<Feature, Integer> weightedSubFeatures;
  
  private final SingleFeatureEvaluator singleFeatureEvaluator;
  
  /**
   * Initializes the sub features to all have equal positive weight
   */
  public EvolvableFeatureEvaluator(Set<Feature> subFeatures) {
    this.weightedSubFeatures = new HashMap<>();
    this.singleFeatureEvaluator = new SingleFeatureEvaluator(this::linearCombination);
    for (Feature subFeature : subFeatures) {
      weightedSubFeatures.put(subFeature, 1);
    }
    normalize();
  }
  
  public EvolvableFeatureEvaluator(Map<Feature, Integer> weigthedSubFeatures) {
    this.weightedSubFeatures = weigthedSubFeatures;
    this.singleFeatureEvaluator = new SingleFeatureEvaluator(this::linearCombination);
    normalize();
  }
  
  @Override
  public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID) {
    return singleFeatureEvaluator.findBest(awaitMoveMessageType, possibleBoardTypes, currentID);
  }
  
  /**
   * Changes all the weights of the sub features by an amount of either + or - stepSize, chosen at
   * random for each weight.
   * 
   * @param stepSize the amount that each weight should change in either positive or negative
   * direction
   */
  void evolve(int stepSize) {
    for (Entry<Feature, Integer> entry : weightedSubFeatures.entrySet()) {
      int delta = random.nextBoolean() ? stepSize : -stepSize;
      entry.setValue(entry.getValue() + delta);
    }
    normalize();
  }
  
  /**
   * @return a deep copy of this object
   */
  EvolvableFeatureEvaluator copy() {
    return new EvolvableFeatureEvaluator(new HashMap<>(weightedSubFeatures));
  }
  
  @Override
  public String toString() {
    return weightedSubFeatures.toString();
  }
  
  private int linearCombination(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType,
      CurrentID currentID) {
    return weightedSubFeatures.entrySet().stream().mapToInt(entry -> {
      int weight = entry.getValue();
      int subFeature = entry.getKey().measure(awaitMoveMessageType, boardType, currentID);
      return weight * subFeature;
    }).sum();
  }
  
  /**
   * Normalizing the weights prevents them from marching in lock step in some random direction.
   */
  private void normalize() {
    double absoluteValueNorm = weightedSubFeatures.values().stream().mapToInt(Math::abs).sum();
    for (Entry<Feature, Integer> entry : weightedSubFeatures.entrySet()) {
      int normalizedValue = (int) (1000 * (entry.getValue() / absoluteValueNorm));
      entry.setValue(normalizedValue);
    }
  }
}
