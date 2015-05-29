package competition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import server.Board;
import server.Game;
import server.Player;

import com.google.inject.Inject;

public class HeadlessServer {
  
  private final Game game;
  
  private final ExecutorService executorService;
  
  private final PlayerFactory playerFactory;
  
  @Inject
  public HeadlessServer(Game game,
      ExecutorService executorService,
      PlayerFactory playerFactory) {
    this.game = game;
    this.executorService = executorService;
    this.playerFactory = playerFactory;
    game.setUserinterface(new MockUI());
  }
  
  public void run() {
    executorService.submit(() -> {
      resetGame();
      runGameLoop();
    });
  }
  
  private void resetGame() {
    HashMap<Integer, Player> playerMap = new HashMap<>();
    for (Player player : playerFactory.createPlayers(game)) {
      playerMap.put(player.getID(), player);
    }
    injectFields(playerMap, new Board());
  }
  
  private void injectFields(HashMap<Integer, Player> players, Board board) {
    try {
      Field spielerMap = Game.class.getDeclaredField("spieler");
      spielerMap.setAccessible(true);
      spielerMap.set(game, players);
      
      Field spielBrett = Game.class.getDeclaredField("spielBrett");
      spielBrett.setAccessible(true);
      spielBrett.set(game, board);
      
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
  }
  
  private void runGameLoop() {
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
