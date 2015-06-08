package competition.featureevaluator;

import java.util.HashSet;
import java.util.Set;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

import com.google.common.collect.ImmutableSet;

import ai.Evaluator;
import ai.featureevaluator.IsStandingOnTreasure;
import ai.featureevaluator.TreasureConnectivity;

/**
 * @author Stefan Klug
 */
public class HierarchicFeatureEvaluator implements Evaluator
{
    // private final DistanceToTreasure distanceToTreasure = new DistanceToTreasure();
    private final IsStandingOnTreasure isStandingOnTreasure = new IsStandingOnTreasure();
    // private final PlayerConnectivity playerConnectivity = new PlayerConnectivity();
    private final TreasureConnectivity treasureConnectivity = new TreasureConnectivity();

    private AwaitMoveMessageType awaitMoveMessageType;
    private ImmutableSet<BoardType> possibleBoardTypes;
    private CurrentID currentID;

    @Override
    public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        this.awaitMoveMessageType = awaitMoveMessageType;
        this.possibleBoardTypes = possibleBoardTypes;
        this.currentID = currentID;

        // TODO Auto-generated method stub
        Set<BoardType> remainingBoards = layer1(possibleBoardTypes);
        remainingBoards = layer2(remainingBoards);
        return remainingBoards.iterator().next();
    }

    private Set<BoardType> layer2(Set<BoardType> inputBoards)
    {
        BoardType best = inputBoards.iterator().next();
        int evalBest = treasureConnectivity.measure(awaitMoveMessageType, best, currentID);
        for (BoardType b : inputBoards)
        {
            int eval = treasureConnectivity.measure(awaitMoveMessageType, b, currentID);
            if (eval > evalBest)
            {
                evalBest = eval;
                best = b;
            }
        }
        HashSet<BoardType> ret = new HashSet<BoardType>();
        ret.add(best);
        return ret;
    }

    private Set<BoardType> layer1(Set<BoardType> inputBoards)
    {
        Set<BoardType> remainingBoards = new HashSet<BoardType>();
        for (BoardType b : inputBoards)
        {
            if (this.isStandingOnTreasure.measure(awaitMoveMessageType, b, currentID) == 1)
            {
                remainingBoards.add(b);
            }
        }
        if (remainingBoards.isEmpty())
        {
            System.out.println("Test: Ziel nicht direkt erreichbar!");
            remainingBoards.addAll(possibleBoardTypes);
        }
        return remainingBoards;
    }
}
