package ai;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.MoveMessageType;

import com.google.common.collect.ImmutableSet;
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
    BoardType oldBoard = awaitMoveMessageType.getBoard();
    
    ImmutableSet<MoveMessageType> allPossibleMoves =
        boardPermuter.createAllPossibleMoves(oldBoard);
    
    return boardEvaluator.findBest(oldBoard, allPossibleMoves);
  }
}
