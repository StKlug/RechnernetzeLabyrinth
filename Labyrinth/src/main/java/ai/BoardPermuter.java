package ai;

import java.util.Set;

import jaxb.BoardType;
import jaxb.CardType;
import jaxb.CardType.Openings;
import jaxb.MoveMessageType;
import jaxb.PositionType;
import util.Misc;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public final class BoardPermuter {
  
  public ImmutableSet<MoveMessageType> createAllMoveMessageTypes(BoardType boardType) {
    Set<CardType> shiftCards = createAllShiftCards(boardType.getShiftCard());
    Set<PositionType> shiftPositions = createAllShiftPositions(boardType.getForbidden());
    Builder<MoveMessageType> builder = ImmutableSet.builder();
    for (CardType shiftCard : shiftCards) {
      for (PositionType shiftPosition : shiftPositions) {
        builder.add(createMoveMessageType(shiftCard, shiftPosition));
      }
    }
    return builder.build();
  }
  
  private ImmutableSet<CardType> createAllShiftCards(CardType cardType) {
    Builder<CardType> builder = ImmutableSet.builder();
    builder.add(cardType);
    for (int i = 0; i < 3; i++) {
      cardType = rotateClockWise(cardType);
      builder.add(cardType);
    }
    return builder.build();
  }
  
  private CardType rotateClockWise(CardType cardType) {
    Openings oldOpenings = cardType.getOpenings();
    Openings rotatedOpenings = new Openings();
    rotatedOpenings.setRight(oldOpenings.isTop());
    rotatedOpenings.setBottom(oldOpenings.isRight());
    rotatedOpenings.setLeft(oldOpenings.isBottom());
    rotatedOpenings.setTop(oldOpenings.isLeft());
    CardType rotatedCardType = new CardType();
    rotatedCardType.setOpenings(rotatedOpenings);
    rotatedCardType.setPin(cardType.getPin());
    rotatedCardType.setTreasure(cardType.getTreasure());
    return rotatedCardType;
  }
  
  private ImmutableSet<PositionType> createAllShiftPositions(PositionType forbidden) {
    Builder<PositionType> builder = ImmutableSet.builder();
    PositionType[] shiftPositions = new PositionType[4];
    for (int index = 1; index <= 6; index += 2) {
      shiftPositions[0] = Misc.createPositionType(0, index);
      shiftPositions[1] = Misc.createPositionType(6, index);
      shiftPositions[2] = Misc.createPositionType(index, 0);
      shiftPositions[3] = Misc.createPositionType(index, 6);
      
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
  
  private MoveMessageType createMoveMessageType(CardType cardType, PositionType shiftPosition) {
    MoveMessageType moveMessageType = new MoveMessageType();
    moveMessageType.setShiftCard(cardType);
    moveMessageType.setShiftPosition(shiftPosition);
    return moveMessageType;
  }
}
