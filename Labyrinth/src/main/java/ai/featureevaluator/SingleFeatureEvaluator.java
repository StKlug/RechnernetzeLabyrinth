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
 * Evaluates every possible board with the same single {@link Feature} and returns the board with
 * the highest {@link Feature#measure} as the winner. Ties are broken with a random number
 * generator. (This has proven useful in training, since otherwise AI's would often get stuck
 * because they kept picking a move that didn't change the player position).
 * <p>
 * Note that even though this class only explicitly evaluates a single Feature, that Feature might
 * call multiple other Features behind the scene via the <a
 * href="http://en.wikipedia.org/wiki/Composite_pattern">Composite pattern</a> or other forms of
 * delegation.
 * 
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
    Loggers.AI.debug("Number of best boards: " + bestBoards.size() + " Score: " + bestScore);
    return bestBoard;
  }
}
