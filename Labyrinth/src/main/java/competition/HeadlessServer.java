package competition;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

import server.Board;
import server.Game;

import com.google.inject.Inject;

/**
 * A gui-less, TCP-less server for the purpose of running many consecutive games.
 * 
 * @author Sebastian Oberhoff
 */
public final class HeadlessServer {
  
  private final Executor executor;
  
  private final PlayerFactory playerFactory;
  
  @Inject
  public HeadlessServer(Executor executor, PlayerFactory playerFactory) {
    this.executor = executor;
    this.playerFactory = playerFactory;
  }
  
  /**
   * Starts a single game in a new thread.
   */
  public void runGame() {
    executor.execute(() -> {
      Game game = createNewGame();
      runGameLoop(game);
    });
  }
  
  /**
   * Instantiates a new game, sets a no-op GUI and reflectively inserts the objects required to
   * substitute TCP communication.
   */
  private Game createNewGame() {
    Game game = new Game();
    game.setUserinterface(new MockUI());
    try {
      Field spielerMap = Game.class.getDeclaredField("spieler");
      spielerMap.setAccessible(true);
      spielerMap.set(game, playerFactory.createPlayerHashMap(game));
      
      Field spielBrett = Game.class.getDeclaredField("spielBrett");
      spielBrett.setAccessible(true);
      spielBrett.set(game, new Board());
      
      Field winner = Game.class.getDeclaredField("winner");
      winner.setAccessible(true);
      winner.set(game, -1);
    }
    catch (NoSuchFieldException
           | SecurityException
           | IllegalArgumentException
           | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return game;
  }
  
  private void runGameLoop(Game game) {
    int currentPlayer = 1;
    while (!game.somebodyWon()) {
      game.singleTurn(currentPlayer);
      currentPlayer++;
      if (currentPlayer == 5) {
        currentPlayer = 1;
      }
    }
    game.cleanUp();
  }
  
}
