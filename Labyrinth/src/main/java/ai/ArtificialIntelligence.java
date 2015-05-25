package ai;

import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.MazeCom;
import jaxb.MoveMessageType;
import util.CurrentID;
import util.MazeComFactory;
import util.Misc;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

public final class ArtificialIntelligence {
  
  private final Random random = new Random();
  
  private final MazeComFactory mazeComFactory;
  
  private final BoardPermuter boardPermuter;
  
  private final CurrentID currentID;
  
  @Inject
  public ArtificialIntelligence(
      MazeComFactory mazeComFactory,
      BoardPermuter boardPermuter,
      CurrentID currentID) {
    this.mazeComFactory = mazeComFactory;
    this.boardPermuter = boardPermuter;
    this.currentID = currentID;
  }
  
  public MazeCom computeMove(AwaitMoveMessageType awaitMoveMessageType) {
    ImmutableSet<MoveMessageType> allMoveMessageTypes =
        boardPermuter.createAllMoveMessageTypes(awaitMoveMessageType.getBoard());
    
    MoveMessageType moveMessageType = allMoveMessageTypes.asList().get(
        random.nextInt(allMoveMessageTypes.size()));
    
    moveMessageType.setNewPinPos(Misc.getPositionType(awaitMoveMessageType.getBoard(),
        currentID.getCurrentID()));
    
    return mazeComFactory.createMoveMessage(moveMessageType);
  }
  
}
