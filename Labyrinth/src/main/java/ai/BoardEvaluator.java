package ai;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.PositionType;
import jaxb.TreasureType;
import util.CurrentID;
import util.ServerFacade;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Class for determining the best possible board from a set of boards.
 * 
 * @author Sebastian Oberhoff
 */
public class BoardEvaluator {
  
  private final CurrentID currentID;
  
  private final Random random = new Random();
  
  @Inject
  public BoardEvaluator(CurrentID currentID) {
    this.currentID = currentID;
  }
  
  /**
   * @return the MoveMessageType with the highest value when applied to a given board according to
   * an implementation specific metric
   */
  public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes) {
    BoardType bestBoard = null;
    int bestScore = Integer.MIN_VALUE;
    
    for (BoardType possibleBoardType : possibleBoardTypes) {
      int score = evaluate(possibleBoardType, awaitMoveMessageType.getTreasure());
      if (bestScore < score) {
        bestBoard = possibleBoardType;
        bestScore = score;
      }
    }
    checkNotNull(bestBoard);
    System.out.print("Value: " + bestScore + " ");
    return bestBoard;
  }
  
  private int evaluate(BoardType boardType, TreasureType treasureType) {
    return random.nextInt(100) + isStandingOnTreasure(treasureType, boardType)
        + (int) (500. / 1 + distanceToTreasure(treasureType, boardType));
  }
  
  private int isStandingOnTreasure(TreasureType treasureType, BoardType boardType) {
    return distanceToTreasure(treasureType, boardType) == 0 ? 10000 : 0;
  }
  
  private int distanceToTreasure(TreasureType treasureType, BoardType boardType) {
    PositionType playerPosition = ServerFacade.findPlayer(boardType, currentID.getCurrentID());
    Optional<PositionType> treasurePosition = ServerFacade.findTreasure(boardType, treasureType);
    if (!treasurePosition.isPresent()) {
      return Integer.MAX_VALUE;
    }
    else {
      int rowDistance = Math.abs(playerPosition.getRow() - treasurePosition.get().getRow());
      int columnDistance = Math.abs(playerPosition.getCol() - treasurePosition.get().getCol());
      return rowDistance + columnDistance;
    }
  }
}
