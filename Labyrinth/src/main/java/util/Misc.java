package util;


import jaxb.PositionType;

/**
 * Stuff that hasn't found a home yet.
 * 
 * @author Sebastian Oberhoff
 */
public final class Misc {
  
  private Misc() {
    // noninstantiable
  }
  
  public static PositionType createPositionType(int rowIndex, int columnIndex) {
    PositionType positionType = new PositionType();
    positionType.setRow(rowIndex);
    positionType.setCol(columnIndex);
    return positionType;
  }
}
