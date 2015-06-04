package ai.featureevaluator;

import java.awt.Point;

import board.LabyrinthBoard;
import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

/**
 * Calculates the number of positions, which can be reached by the player (with currentID)
 * 
 * @author Stefan Klug
 */
public class PlayerConnectivity implements Feature
{
    @Override
    public int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID)
    {
        LabyrinthBoard board = new LabyrinthBoard(boardType);
        Point playerPosition = board.getPosition(currentID.getCurrentID());
        return board.reachablePositions(playerPosition).size();
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName();
    }
}
