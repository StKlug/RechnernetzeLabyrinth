package ai.featureevaluator;

import java.util.Optional;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.PositionType;
import util.CurrentID;
import util.Loggers;
import util.Misc;
import util.ServerFacade;

/**
 * Computes the distance from the player position to the current target treasure in the Manhattan
 * norm (row distance + column distance).
 * 
 * @author Sebastian Oberhoff
 */
public final class DistanceToTreasure implements Feature
{
    @Override
    public int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID)
    {
        PositionType playerPosition = ServerFacade.findPlayer(boardType, currentID.getCurrentID());
        Optional<PositionType> possibleTreasurePosition = ServerFacade.findTreasure(boardType, awaitMoveMessageType.getTreasure());
        Integer distance = possibleTreasurePosition.map(treasurePosition -> Misc.computeDistance(playerPosition, treasurePosition)).orElse(Integer.MAX_VALUE);
        Loggers.FEATURE.debug("distanceToTreasure: " + distance);
        return distance;
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName();
    }
}
