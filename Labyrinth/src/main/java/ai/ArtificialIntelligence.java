package ai;

import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.MoveMessageType;
import util.CurrentID;
import util.Misc;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * The top component of the artificial intelligence module. This class is responsible for coming up
 * with an appropriate MoveMessageType given an AwaitMoveMessageType.
 * 
 * @author Sebastian Oberhoff
 */
public final class ArtificialIntelligence {
  
  private final Random random = new Random();
  
  private final BoardPermuter boardPermuter;
  
  private final CurrentID currentID;
  
  @Inject
  public ArtificialIntelligence(BoardPermuter boardPermuter, CurrentID currentID) {
    this.boardPermuter = boardPermuter;
    this.currentID = currentID;
  }
  
  public MoveMessageType computeMove(AwaitMoveMessageType awaitMoveMessageType) {
    ImmutableSet<MoveMessageType> allMoveMessageTypes =
        boardPermuter.createAllMoveMessageTypes(awaitMoveMessageType.getBoard());
    
    MoveMessageType moveMessageType = allMoveMessageTypes.asList().get(
        random.nextInt(allMoveMessageTypes.size()));
    
    moveMessageType.setNewPinPos(Misc.getPositionType(awaitMoveMessageType.getBoard(),
        currentID.getCurrentID()));
    
    return moveMessageType;
  }
  
}
