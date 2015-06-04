package ai.featureevaluator;

import java.awt.Point;

import board.LabyrinthBoard;
import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.TreasureType;
import util.CurrentID;

/**
 * Calculates the number of positions from which the treasure can be reached
 * 
 * @author Stefan Klug
 */
public class TreasureConnectivity implements Feature
{
    @Override
    public int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID)
    {
        TreasureType treasure = awaitMoveMessageType.getTreasure();
        LabyrinthBoard board = new LabyrinthBoard(boardType);
        Point treasurePosition = board.getPosition(treasure);
        return board.reachablePositions(treasurePosition).size();
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName();
    }
}
