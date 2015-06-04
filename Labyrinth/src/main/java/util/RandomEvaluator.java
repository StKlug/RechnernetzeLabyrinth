package util;

import java.util.Random;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import ai.Evaluator;

import com.google.common.collect.ImmutableSet;

/**
 * Evaluator that picks a board at random. Intended for testing.
 * 
 * @author Sebastian Oberhoff
 */
public final class RandomEvaluator implements Evaluator
{
    Random random = new Random();

    @Override
    public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        return possibleBoardTypes.asList().get(random.nextInt(possibleBoardTypes.size()));
    }
}
