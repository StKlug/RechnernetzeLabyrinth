package ai;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.PositionType;
import jaxb.TreasureType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CurrentID;
import util.Misc;
import util.ServerFacade;

import com.google.common.collect.ImmutableSet;

/**
 * Standard implementation for determining the best possible board from a set of boards.
 * 
 * @author Sebastian Oberhoff
 */
public class StandardBoardEvaluator implements BoardEvaluator {
  
  private final Logger logger = LoggerFactory.getLogger(StandardBoardEvaluator.class);
  
  private final Random random = new Random();
  
  /**
   * @return the MoveMessageType with the highest value when applied to a given board according to
   * an implementation specific metric
   */
  @Override
  public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID) {
    BoardType bestBoard = null;
    int bestScore = Integer.MIN_VALUE;
    
    for (BoardType possibleBoardType : possibleBoardTypes) {
      int score = evaluate(possibleBoardType, awaitMoveMessageType.getTreasure(), currentID);
      if (bestScore < score) {
        bestBoard = possibleBoardType;
        bestScore = score;
      }
    }
    checkNotNull(bestBoard);
    logger.debug("Value: " + bestScore);
    return bestBoard;
  }
  
  private int evaluate(BoardType boardType, TreasureType treasureType, CurrentID currentID) {
    return random.nextInt(100) + isStandingOnTreasure(treasureType, boardType, currentID)
        + (int) (500. / 1 + distanceToTreasure(treasureType, boardType, currentID));
  }
  
  private int isStandingOnTreasure(TreasureType treasureType, BoardType boardType,
      CurrentID currentID) {
    return distanceToTreasure(treasureType, boardType, currentID) == 0 ? 10000 : 0;
  }
  
  private int distanceToTreasure(TreasureType treasureType, BoardType boardType, CurrentID currentID) {
    PositionType playerPosition = ServerFacade.findPlayer(boardType, currentID.getCurrentID());
    Optional<PositionType> possibleTreasurePosition = ServerFacade.findTreasure(boardType,
        treasureType);
    Integer distance = possibleTreasurePosition
        .map(treasurePosition -> Misc.computeDistance(playerPosition, treasurePosition))
        .orElse(Integer.MAX_VALUE);
    return distance;
  }
}
