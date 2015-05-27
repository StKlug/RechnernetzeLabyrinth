package ai;

import java.util.Set;

import jaxb.BoardType;
import jaxb.CardType;
import jaxb.CardType.Openings;
import jaxb.MoveMessageType;
import jaxb.PositionType;
import util.CurrentID;
import util.Misc;
import util.ServerFacade;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * Computes all possible MoveMessages that can be constructed from a given board. The algorithm
 * considers all 4 rotations of the shift card, all 12 possible insertion points minus the insertion
 * point forbidden by the previous move, as well as all reachable positions by the player pin after
 * the shift has been applied.
 * 
 * @author Sebastian Oberhoff
 */
public final class BoardPermuter {
  
  private final CurrentID currentID;
  
  @Inject
  public BoardPermuter(CurrentID currentID) {
    this.currentID = currentID;
  }
  
  /**
   * @param boardType the current state of the game
   * @return a map of all possible next states, once as BoardTypes, once as the MoveMessageTypes
   * that lead to those BoardTypes when applied to the current board
   */
  public ImmutableBiMap<BoardType, MoveMessageType> createAllPossibleMoves(BoardType boardType) {
    Set<CardType> shiftCards = createAllPossibleShiftCards(boardType.getShiftCard());
    Set<PositionType> shiftPositions = createAllPossibleShiftPositions(boardType.getForbidden());
    
    ImmutableBiMap.Builder<BoardType, MoveMessageType> builder = ImmutableBiMap.builder();
    for (CardType shiftCard : shiftCards) {
      for (PositionType shiftPosition : shiftPositions) {
        Set<PositionType> newPinPositions = createAllPossibleNewPinPositions(boardType, shiftCard,
            shiftPosition);
        for (PositionType newPinPos : newPinPositions) {
          MoveMessageType nextMove = createMoveMessageType(shiftCard, shiftPosition, newPinPos);
          BoardType nextBoard = ServerFacade.convertMessageToBoardType(boardType, nextMove);
          builder.put(nextBoard, nextMove);
        }
      }
    }
    return builder.build();
  }
  
  /**
   * @return the 4 possible rotations of the card
   */
  private ImmutableSet<CardType> createAllPossibleShiftCards(CardType cardType) {
    Builder<CardType> builder = ImmutableSet.builder();
    builder.add(cardType);
    for (int i = 0; i < 3; i++) {
      cardType = rotateClockWise(cardType);
      builder.add(cardType);
    }
    return builder.build();
  }
  
  /**
   * @return the set of possible insertion points minus the insertion point forbidden by the
   * previous move
   */
  private ImmutableSet<PositionType> createAllPossibleShiftPositions(PositionType forbidden) {
    Builder<PositionType> builder = ImmutableSet.builder();
    PositionType[] shiftPositions = new PositionType[4];
    for (int index = 1; index <= 6; index += 2) {
      shiftPositions[0] = Misc.createPositionType(0, index); // first row
      shiftPositions[1] = Misc.createPositionType(6, index); // last row
      shiftPositions[2] = Misc.createPositionType(index, 0); // first column
      shiftPositions[3] = Misc.createPositionType(index, 6); // last column
      
      for (PositionType shiftPosition : shiftPositions) {
        if (isAllowed(forbidden, shiftPosition)) {
          builder.add(shiftPosition);
        }
      }
    }
    return builder.build();
  }
  
  /**
   * @return the set of of positions that can be reached by our player pin after applying the shift
   */
  private ImmutableSet<PositionType> createAllPossibleNewPinPositions(BoardType boardType,
      CardType shiftCard, PositionType shiftPosition) {
    PositionType preShiftPosition = ServerFacade.findPlayer(boardType, currentID.getCurrentID());
    MoveMessageType shiftMessage = createMoveMessageType(shiftCard, shiftPosition, preShiftPosition);
    BoardType shiftBoard = ServerFacade.convertMessageToBoardType(boardType, shiftMessage);
    PositionType postShiftPosition = ServerFacade.findPlayer(shiftBoard, currentID.getCurrentID());
    return ServerFacade.computeReachablePositions(shiftBoard, postShiftPosition);
  }
  
  /**
   * Creates a copy of the card rotated clockwise once. If there are players or treasures on the
   * card, those are also copied.
   */
  private CardType rotateClockWise(CardType cardType) {
    Openings oldOpenings = cardType.getOpenings();
    Openings rotatedOpenings = new Openings();
    
    rotatedOpenings.setRight(oldOpenings.isTop());
    rotatedOpenings.setBottom(oldOpenings.isRight());
    rotatedOpenings.setLeft(oldOpenings.isBottom());
    rotatedOpenings.setTop(oldOpenings.isLeft());
    
    CardType rotatedCardType = new CardType();
    rotatedCardType.setOpenings(rotatedOpenings);
    // redundant? I don't think there can be players on the shift card
    rotatedCardType.setPin(cardType.getPin());
    rotatedCardType.setTreasure(cardType.getTreasure());
    return rotatedCardType;
  }
  
  private boolean isAllowed(PositionType forbidden, PositionType shiftPosition) {
    return forbidden == null || shiftPosition.getRow() != forbidden.getRow()
        || shiftPosition.getCol() != forbidden.getCol();
  }
  
  private MoveMessageType createMoveMessageType(CardType cardType, PositionType shiftPosition,
      PositionType newPinPos) {
    MoveMessageType moveMessageType = new MoveMessageType();
    moveMessageType.setShiftCard(cardType);
    moveMessageType.setShiftPosition(shiftPosition);
    moveMessageType.setNewPinPos(newPinPos);
    return moveMessageType;
  }
}
