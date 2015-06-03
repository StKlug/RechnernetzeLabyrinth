package util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import jaxb.BoardType;
import jaxb.CardType;
import jaxb.MoveMessageType;
import jaxb.PositionType;
import jaxb.TreasureType;
import server.Board;

import com.google.common.collect.ImmutableSet;

/**
 * Facade for reused server source code.
 * 
 * @author Sebastian Oberhoff
 */
public final class ServerFacade {
  
  private ServerFacade() {
    // noninstantiable
  }
  
  /**
   * Applies a MoveMessageType to a BoardType and returns the resulting BoardType. This operation
   * doesn't change the old board.
   */
  public static BoardType applyMessageToBoard(BoardType oldBoard,
      MoveMessageType moveMessageType, int id) {
    Board board = new Board(oldBoard);
    board.proceedTurn(moveMessageType, id);
    return board;
  }
  
  public static BoardType applyShiftToBoard(BoardType oldBoard, CardType shiftCard,
      PositionType shiftPosition) {
    Board board = new Board(oldBoard);
    MoveMessageType shiftMessage = new MoveMessageType();
    shiftMessage.setShiftCard(shiftCard);
    shiftMessage.setShiftPosition(shiftPosition);
    board.proceedShift(shiftMessage);
    return board;
  }
  
  public static ImmutableSet<PositionType> computeReachablePositions(
      BoardType boardType, PositionType currentPosition) {
    Board board = convertBoardTypeToBoard(boardType);
    return ImmutableSet.copyOf(board.getAllReachablePositions(currentPosition));
  }
  
  public static PositionType findPlayer(BoardType boardType, int id) {
    Board board = convertBoardTypeToBoard(boardType);
    return checkNotNull(board.findPlayer(id));
  }
  
  public static Optional<PositionType> findTreasure(BoardType boardType, TreasureType treasureType) {
    Board board = convertBoardTypeToBoard(boardType);
    return Optional.ofNullable(board.findTreasure(treasureType));
  }
  
  private static Board convertBoardTypeToBoard(BoardType boardType) {
    return boardType instanceof Board ? (Board) boardType : new Board(boardType);
  }
}
