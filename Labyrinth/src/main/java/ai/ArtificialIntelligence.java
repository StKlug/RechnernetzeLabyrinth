package ai;

import java.awt.Point;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.MoveMessageType;
import util.CurrentID;
import util.Loggers;
import util.Misc;
import board.LabyrinthBoard;

import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Inject;

/**
 * The top component of the artificial intelligence module. This class is responsible for coming up
 * with an appropriate MoveMessageType given an AwaitMoveMessageType.
 * 
 * @author Sebastian Oberhoff
 */
public final class ArtificialIntelligence
{
    private final BoardPermuter boardPermuter;

    private final Evaluator evaluator;

    private final CurrentID currentID;

    private int foundTreasures = 0;

    @Inject
    public ArtificialIntelligence(BoardPermuter boardPermuter, Evaluator evaluator, CurrentID currentID)
    {
        this.boardPermuter = boardPermuter;
        this.evaluator = evaluator;
        this.currentID = currentID;
    }

    public MoveMessageType computeMove(AwaitMoveMessageType awaitMoveMessageType)
    {
        ImmutableBiMap<BoardType, MoveMessageType> nextStates = boardPermuter.createAllPossibleMoves(awaitMoveMessageType.getBoard());
        BoardType bestBoard = evaluator.findBest(awaitMoveMessageType, nextStates.keySet(), currentID);
        MoveMessageType bestMove = nextStates.get(bestBoard);

        LabyrinthBoard newBoard = new LabyrinthBoard(bestBoard);
        Point posTreasure = newBoard.getPosition(awaitMoveMessageType.getTreasure());
        Point posPlayer = newBoard.getPosition(this.currentID.getCurrentID());
        if (posPlayer.equals(posTreasure))
        {
            this.foundTreasures++;
        }

        Loggers.AI.debug("Shift: " + Misc.printPosition(bestMove.getShiftPosition()) + ", Player Position: " + Misc.printPosition(bestMove.getNewPinPos()));

        return bestMove;
    }

    public int getFoundTreasures()
    {
        return this.foundTreasures;
    }
}
