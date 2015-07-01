package ai.featureevaluator;

import java.awt.Point;

import board.LabyrinthBoard;
import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.TreasureType;
import util.CurrentID;

/**
 * @author Stefan Klug
 */
public class CustomDistanceToTreasure implements Feature
{
    @Override
    public int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID)
    {
        TreasureType treasure = awaitMoveMessageType.getTreasure();
        LabyrinthBoard board = new LabyrinthBoard(boardType);
        Point playerPosition = board.getPosition(currentID.getCurrentID());
        Point treasurePosition = board.getPosition(treasure);
        int treasureCard = board.toInt(treasurePosition);
        int eval = 0;

        if (board.isBitSet(treasureCard, LabyrinthBoard.INDEX_DOWN))
        {
            if (playerPosition.x > treasurePosition.x)
            {
                eval += (7 - (playerPosition.x - treasurePosition.x));
            }
        }
        else
        {
            if (playerPosition.x > treasurePosition.x)
            {
                // schlechte Bewertung
            }
            else
            {
                eval += 5; // besser
            }
        }

        if (board.isBitSet(treasureCard, LabyrinthBoard.INDEX_UP))
        {
            if (playerPosition.x < treasurePosition.x)
            {
                eval += (7 - (treasurePosition.x - playerPosition.x));
            }
        }
        else
        {
            if (playerPosition.x < treasurePosition.x)
            {
                // schlechte Bewertung
            }
            else
            {
                eval += 5; // besser
            }
        }
        
        if (board.isBitSet(treasureCard, LabyrinthBoard.INDEX_LEFT))
        {
            if (playerPosition.y < treasurePosition.y)
            {
                eval += (7 - (treasurePosition.y - playerPosition.y));
            }
        }
        else
        {
            if (playerPosition.y < treasurePosition.y)
            {
                // schlechte Bewertung
            }
            else
            {
                eval += 5; // besser
            }
        }
        
        if (board.isBitSet(treasureCard, LabyrinthBoard.INDEX_RIGHT))
        {
            if (treasurePosition.y < playerPosition.y)
            {
                eval += (7 - (playerPosition.y - treasurePosition.y));
            }
        }
        else
        {
            if (treasurePosition.y < playerPosition.y)
            {
                // schlechte Bewertung
            }
            else
            {
                eval += 5; // besser
            }
        }

        return eval;
    }
}
