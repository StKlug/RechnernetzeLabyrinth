package ai;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

import com.google.common.collect.ImmutableSet;

/**
 * A Evaluator has the responsibility to find a best possible next state of the board that the
 * AI should choose for its move.
 * 
 * @author Sebastian Oberhoff
 */
@FunctionalInterface
public interface Evaluator {
  
  /**
   * @param awaitMoveMessageType this is the relevant piece of the message that was transmitted by
   * the server. It contains such information as the current state of the board, the currently
   * sought treasure and a list of remaining treasures.
   * @param possibleBoardTypes the set of possible future board states. This set may not contain any
   * illegal moves.
   * @param currentID the current ID of the player
   * @return the board that the AI should pick as its next move
   */
  BoardType findBest(AwaitMoveMessageType awaitMoveMessageType,
      ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID);
}
