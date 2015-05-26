package ai;

import java.util.Set;

import jaxb.BoardType;
import jaxb.CardType;
import jaxb.CardType.Openings;
import jaxb.MoveMessageType;
import jaxb.PositionType;
import util.CurrentID;
import util.Misc;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * Computes all possible MoveMessages that can be constructed from a given board. The algorithm
 * considers all 4 rotations of the shift card, as well as all 12 possible insertion points minus
 * the insertion point forbidden by the previous move.
 * <p>
 * By returning MoveMessageTypes rather than BoardTypes, it's far easier to create the response than
 * it would be if the correct MoveMessageType had to be inferred at a later point.
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
   * <b>The returned MoveMessageTypes will keep the current player position</b>
   * 
   * @param boardType the current state of the game
   * @return the set of all possible next states in the form of MoveMessageTypes
   */
  public ImmutableSet<MoveMessageType> createAllPossibleMoves(BoardType boardType) {
    Set<CardType> shiftCards = createAllPossibleShiftCards(boardType.getShiftCard());
    Set<PositionType> shiftPositions = createAllPossibleShiftPositions(boardType.getForbidden());
    Builder<MoveMessageType> builder = ImmutableSet.builder();
    for (CardType shiftCard : shiftCards) {
      for (PositionType shiftPosition : shiftPositions) {
        builder.add(createMoveMessageType(shiftCard, shiftPosition,
            Misc.getPositionType(boardType, currentID.getCurrentID())));
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
