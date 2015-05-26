package ai;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;

import jaxb.BoardType;
import jaxb.MoveMessageType;
import server.Board;

import com.google.common.collect.ImmutableSet;

/**
 * Class for determining the best possible board from a set of boards.
 * 
 * @author Sebastian Oberhoff
 */
public class BoardEvaluator {
  
  private final Random random = new Random();
  
  /**
   * @return the board with the highest value according the an implementation specific metric
   */
  public MoveMessageType findBest(BoardType boardType,
      ImmutableSet<MoveMessageType> moveMessageTypes) {
    MoveMessageType bestMove = null;
    int bestScore = Integer.MIN_VALUE;
    for (MoveMessageType moveMessageType : moveMessageTypes) {
      int score = evaluate(convertMessageToBoardType(boardType, moveMessageType));
      if (bestScore < score) {
        bestMove = moveMessageType;
        bestScore = score;
      }
    }
    checkNotNull(bestMove);
    return bestMove;
  }
  
  /**
   * Applies a MoveMessageType to a BoardType and returns the resulting BoardType. This operation
   * doesn't change the old board.
   */
  private BoardType convertMessageToBoardType(BoardType oldBoard, MoveMessageType moveMessageType) {
    Board board = new Board(oldBoard);
    board.proceedShift(moveMessageType);
    return board;
  }
  
  private int evaluate(BoardType boardType) {
    return random.nextInt(100);
  }
}
