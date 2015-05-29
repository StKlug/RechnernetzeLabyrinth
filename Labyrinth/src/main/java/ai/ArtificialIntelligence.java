package ai;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.MoveMessageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CurrentID;
import util.Misc;

import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Inject;

/**
 * The top component of the artificial intelligence module. This class is responsible for coming up
 * with an appropriate MoveMessageType given an AwaitMoveMessageType.
 * 
 * @author Sebastian Oberhoff
 */
public final class ArtificialIntelligence {
  
  private final Logger logger = LoggerFactory.getLogger(ArtificialIntelligence.class);
  
  private final BoardPermuter boardPermuter;
  
  private final BoardEvaluator boardEvaluator;
  
  private final CurrentID currentID;
  
  @Inject
  public ArtificialIntelligence(
      BoardPermuter boardPermuter,
      BoardEvaluator boardEvaluator,
      CurrentID currentID) {
    this.boardPermuter = boardPermuter;
    this.boardEvaluator = boardEvaluator;
    this.currentID = currentID;
  }
  
  public MoveMessageType computeMove(AwaitMoveMessageType awaitMoveMessageType) {
    ImmutableBiMap<BoardType, MoveMessageType> nextStates =
        boardPermuter.createAllPossibleMoves(awaitMoveMessageType.getBoard());
    BoardType bestBoard =
        boardEvaluator.findBest(awaitMoveMessageType, nextStates.keySet(), currentID);
    MoveMessageType bestMove = nextStates.get(bestBoard);
    
    logger.debug("Shift: " + Misc.printPosition(bestMove.getShiftPosition())
        + "Player Position: " + Misc.printPosition(bestMove.getNewPinPos()));
    
    return bestMove;
  }
}
