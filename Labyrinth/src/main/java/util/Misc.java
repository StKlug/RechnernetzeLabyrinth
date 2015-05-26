package util;

import java.util.NoSuchElementException;

import jaxb.BoardType;
import jaxb.PositionType;
import jaxb.BoardType.Row;

/**
 * Stuff that hasn't found a home yet.
 * 
 * @author Sebastian Oberhoff
 */
public final class Misc {
  
  private Misc() {
    // noninstantiable
  }
  
  public static PositionType getPositionType(BoardType boardType, int id) {
    for (int rowIndex = 0; rowIndex < boardType.getRow().size(); rowIndex++) {
      Row row = boardType.getRow().get(rowIndex);
      for (int columnIndex = 0; columnIndex < row.getCol().size(); columnIndex++) {
        if (row.getCol().get(columnIndex).getPin().getPlayerID().contains(id)) {
          return createPositionType(rowIndex, columnIndex);
        }
      }
    }
    throw new NoSuchElementException("Position of player with ID " + id + " not found.");
  }
  
  public static PositionType createPositionType(int rowIndex, int columnIndex) {
    PositionType positionType = new PositionType();
    positionType.setRow(rowIndex);
    positionType.setCol(columnIndex);
    return positionType;
  }
}
