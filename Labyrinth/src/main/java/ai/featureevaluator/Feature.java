package ai.featureevaluator;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import util.CurrentID;

/**
 * "A feature is any higher level representation of the raw input" - Yaser S. Abu-Mostafa
 * <p>
 * Higher level representation in this case means mapping a state of the game to an int. One could
 * also interpret such a Feature as a norm.
 * <p>
 * The state of the game analyzed by a Feature might be either the current state or a hypothetical
 * future state (the implementation of this interface shouldn't have any knowledge about which).
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Feature_(machine_learning)">Feature - Wikipedia</a>
 * @author Sebastian Oberhoff
 */
@FunctionalInterface
public interface Feature {
  
  /**
   * Translates the state of the game, which is quite complex, into an int which can easily be used
   * for comparison.
   * 
   * @param awaitMoveMessageType this is the relevant piece of the message that was transmitted by
   * the server. It contains such information as the current state of the board, the currently
   * sought treasure and a list of remaining treasures.
   * @param possibleBoardTypes the set of possible future board states. This set may not contain any
   * illegal moves.
   * @param currentID the current ID of the player
   * @return an int representation of the board
   */
  int measure(AwaitMoveMessageType awaitMoveMessageType, BoardType boardType, CurrentID currentID);
}
