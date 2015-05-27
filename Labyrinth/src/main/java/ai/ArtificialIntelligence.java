package ai;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.MoveMessageType;
import jaxb.PositionType;

import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Inject;

/**
 * The top component of the artificial intelligence module. This class is responsible for coming up
 * with an appropriate MoveMessageType given an AwaitMoveMessageType.
 * 
 * @author Sebastian Oberhoff
 */
public final class ArtificialIntelligence {
  
  private final BoardPermuter boardPermuter;
  
  private final BoardEvaluator boardEvaluator;
  
  @Inject
  public ArtificialIntelligence(BoardPermuter boardPermuter, BoardEvaluator boardEvaluator) {
    this.boardPermuter = boardPermuter;
    this.boardEvaluator = boardEvaluator;
  }
  
  public MoveMessageType computeMove(AwaitMoveMessageType awaitMoveMessageType) {
    ImmutableBiMap<BoardType, MoveMessageType> nextStates = boardPermuter
        .createAllPossibleMoves(awaitMoveMessageType.getBoard());
    BoardType bestBoard = boardEvaluator.findBest(awaitMoveMessageType, nextStates.keySet());
    MoveMessageType bestMove = nextStates.get(bestBoard);
    log(bestMove);
    return bestMove;
  }
  
  private void log(MoveMessageType bestMove) {
    PositionType shiftPosition = bestMove.getShiftPosition();
    PositionType newPinPos = bestMove.getNewPinPos();
    System.out.println("Shift: (" + shiftPosition.getRow() + ", " + shiftPosition.getCol() + ") "
        + "Player Position: (" + newPinPos.getRow() + ", " + newPinPos.getCol() + ")");
  }
}
