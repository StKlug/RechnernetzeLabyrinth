package ai.featureevaluator;

import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;
import ai.Evaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Evaluates every possible board with the same single {@link Feature} and returns the board with
 * the highest {@link Feature#measure} as the winner. Ties are broken with a random number
 * generator. (This has proven useful in training, since otherwise AI's would often get stuck
 * because they kept picking a move that didn't change the player position).
 * <p>
 * Note that even though this class only explicitly evaluates a single Feature, that Feature might call multiple other
 * Features behind the scene via the <a href="http://en.wikipedia.org/wiki/Composite_pattern">Composite pattern</a> or
 * other forms of delegation.
 * 
 * @author Sebastian Oberhoff, Stefan Klug
 */
public final class SingleFeatureEvaluator implements Evaluator
{
    private final Feature feature;
    private Random random = new Random();

    public SingleFeatureEvaluator(Feature feature)
    {
        this.feature = feature;
    }

    @Override
    public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        ImmutableSet<BoardType> bests = findBests(awaitMoveMessageType, possibleBoardTypes, currentID);
        int index = random.nextInt(bests.size());
        for (BoardType best : bests)
        {
            if (index == 0) { return best; }
            index--;
        }
        return null; // Compiler-Dummy, which cannot be reached
    }

    public ImmutableSet<BoardType> findBests(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        Builder<BoardType> builder = ImmutableSet.builder();
        int evalBest = Integer.MIN_VALUE;
        for (BoardType possibleBoard : possibleBoardTypes)
        {
            int eval = feature.measure(awaitMoveMessageType, possibleBoard, currentID);
            if (eval == evalBest)
            {
                builder.add(possibleBoard);
            }
            else if (eval > evalBest)
            {
                builder = ImmutableSet.builder();
                evalBest = eval;
                builder.add(possibleBoard);
            }
        }
        return builder.build();
    }
}
