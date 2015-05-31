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

final class EvolvableFeatureEvaluator implements Evaluator {
  
  private static final Random random = new Random();
  
  private final Map<Feature, Integer> weightedSubFeatures;
  
  private final SingleFeatureEvaluator singleFeatureEvaluator;
  
  EvolvableFeatureEvaluator(Set<Feature> subFeatures) {
    this(new HashMap<>());
    for (Feature subFeature : subFeatures) {
      weightedSubFeatures.put(subFeature, 1);
    }
  }
  
  EvolvableFeatureEvaluator(Map<Feature, Integer> weigthedSubFeatures) {
    this.weightedSubFeatures = weigthedSubFeatures;
    this.singleFeatureEvaluator = new SingleFeatureEvaluator(this::linearCombination);
  }
  
  @Override
  public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID) {
    return singleFeatureEvaluator.findBest(awaitMoveMessageType, possibleBoardTypes, currentID);
  }
  
  void evolve(int stepSize) {
    for (Entry<Feature, Integer> entry : weightedSubFeatures.entrySet()) {
      int delta = random.nextBoolean() ? stepSize : -stepSize;
      entry.setValue(entry.getValue() + delta);
    }
    normalize();
  }
  
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
  
  private void normalize() {
    double absoluteValueNorm = weightedSubFeatures.values().stream().mapToInt(Math::abs).sum();
    for (Entry<Feature, Integer> entry : weightedSubFeatures.entrySet()) {
      int normalizedValue = (int) (1000 * (entry.getValue() / absoluteValueNorm));
      entry.setValue(normalizedValue);
    }
  }
}
