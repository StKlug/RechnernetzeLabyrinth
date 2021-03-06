package competition.featureevaluator;

import java.util.Set;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import ai.Evaluator;
import ai.featureevaluator.CustomDistanceToTreasure;
import ai.featureevaluator.IsStandingOnTreasure;
import ai.featureevaluator.PlayerConnectivity;
import ai.featureevaluator.SingleFeatureEvaluator;
import ai.featureevaluator.TreasureConnectivity;

/**
 * @author Stefan Klug
 */
public class HierarchicFeatureEvaluator implements Evaluator
{
    // private final DistanceToTreasure distanceToTreasure = new DistanceToTreasure();
    private final IsStandingOnTreasure isStandingOnTreasure = new IsStandingOnTreasure();
    private final PlayerConnectivity playerConnectivity = new PlayerConnectivity();
    private final TreasureConnectivity treasureConnectivity = new TreasureConnectivity();
    private final CustomDistanceToTreasure customDistance = new CustomDistanceToTreasure();

    private AwaitMoveMessageType awaitMoveMessageType;
    private CurrentID currentID;

    @Override
    public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        this.awaitMoveMessageType = awaitMoveMessageType;
        this.currentID = currentID;

        ImmutableSet<BoardType> treasureBoards = getTreasureBoards(possibleBoardTypes);
        if (treasureBoards.isEmpty())
        {
            Set<BoardType> bestBoards = new SingleFeatureEvaluator(this.playerConnectivity).findBests(awaitMoveMessageType, possibleBoardTypes, currentID);
            ImmutableSet.Builder<BoardType> builder = new Builder<BoardType>();
            for (BoardType board : bestBoards)
            {
                builder.add(board);
            }
            return new SingleFeatureEvaluator(customDistance).findBest(awaitMoveMessageType, builder.build(), currentID);
        }
        else
        {
            return new SingleFeatureEvaluator(this.treasureConnectivity).findBest(awaitMoveMessageType, treasureBoards, currentID);
        }
    }

    private ImmutableSet<BoardType> getTreasureBoards(Set<BoardType> inputBoards)
    {
        Builder<BoardType> builder = ImmutableSet.builder();
        for (BoardType b : inputBoards)
        {
            if (this.isStandingOnTreasure.measure(this.awaitMoveMessageType, b, this.currentID) == 1)
            {
                builder.add(b);
            }
        }
        return builder.build();
    }
}
