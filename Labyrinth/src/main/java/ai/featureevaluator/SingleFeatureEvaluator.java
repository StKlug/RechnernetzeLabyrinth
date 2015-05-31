package ai.featureevaluator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;
import util.Loggers;
import ai.Evaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Sebastian Oberhoff
 */
public final class SingleFeatureEvaluator implements Evaluator {
  
  private final Random random = new Random();
  
  private final Feature feature;
  
  public SingleFeatureEvaluator(Feature feature) {
    this.feature = feature;
  }
  
  @Override
  public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID) {
    Set<BoardType> bestBoards = new HashSet<>();
    int bestScore = Integer.MIN_VALUE;
    
    for (BoardType possibleBoardType : possibleBoardTypes) {
      int score = feature.measure(awaitMoveMessageType, possibleBoardType, currentID);
      if (bestScore <= score) {
        bestBoards.add(possibleBoardType);
        bestScore = score;
      }
    }
    BoardType bestBoard = Iterables.get(bestBoards, random.nextInt(bestBoards.size()));
    Loggers.AI.debug("Number of best boards: "+ bestBoards.size() + " Score: " + bestScore);
    return bestBoard;
  }
}
