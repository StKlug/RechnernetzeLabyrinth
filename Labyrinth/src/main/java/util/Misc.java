package util;

import jaxb.PositionType;

/**
 * Stuff that hasn't found a home yet.
 * 
 * @author Sebastian Oberhoff
 */
public final class Misc
{
    private Misc()
    {
        // noninstantiable
    }

    public static PositionType createPositionType(int rowIndex, int columnIndex)
    {
        PositionType positionType = new PositionType();
        positionType.setRow(rowIndex);
        positionType.setCol(columnIndex);
        return positionType;
    }

    public static int computeDistance(PositionType firstPosition, PositionType secondPosition)
    {
        int rowDistance = Math.abs(firstPosition.getRow() - secondPosition.getRow());
        int columnDistance = Math.abs(firstPosition.getCol() - secondPosition.getCol());
        return rowDistance + columnDistance;
    }

    public static String printPosition(PositionType positionType)
    {
        return "(" + positionType.getRow() + ", " + positionType.getCol() + ")";
    }
}
