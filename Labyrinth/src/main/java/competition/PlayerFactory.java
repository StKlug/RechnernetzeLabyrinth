package competition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import jaxb.MazeCom;
import jaxb.TreasureType;
import networking.Connection;
import server.Game;
import server.Player;

import com.google.inject.Inject;

/**
 * Factory for creating a new batch of Player-objects for another game, reusing existing queues to
 * communicate with clients.
 * 
 * @author Sebastian Oberhoff
 */
public final class PlayerFactory
{
    private final BlockingQueue<MazeCom> clientToServer;

    private final Collection<BlockingQueue<MazeCom>> serverToClientQueues;

    @Inject
    public PlayerFactory(BlockingQueue<MazeCom> clientToServer, Collection<BlockingQueue<MazeCom>> serverToClientQueues)
    {
        this.clientToServer = clientToServer;
        this.serverToClientQueues = serverToClientQueues;
    }

    /**
     * Exchanges a new round of logins with the clients and returns a HashMap from the player IDs to
     * the player objects. The return type was chosen to be compatible with the "spieler" field in the {@link Game}
     * class.
     */
    public HashMap<Integer, Player> createPlayerHashMap(Game game)
    {
        HashMap<Integer, Player> playerHashMap = new HashMap<>();
        int playerID = 1;
        for (BlockingQueue<MazeCom> outputQueue : serverToClientQueues)
        {
            Connection connection = new QueueConnection(game, playerID, clientToServer, outputQueue);
            playerHashMap.put(playerID, connection.login(playerID));
            playerID++;
        }
        distributeTreasures(playerHashMap.values());
        return playerHashMap;
    }

    /**
     * Copy-pasted from the server source code.
     */
    private void distributeTreasures(Collection<Player> players)
    {
        ArrayList<TreasureType> treasureCardPile = new ArrayList<TreasureType>();
        treasureCardPile.add(TreasureType.SYM_01);
        treasureCardPile.add(TreasureType.SYM_02);
        treasureCardPile.add(TreasureType.SYM_03);
        treasureCardPile.add(TreasureType.SYM_04);
        treasureCardPile.add(TreasureType.SYM_05);
        treasureCardPile.add(TreasureType.SYM_06);
        treasureCardPile.add(TreasureType.SYM_07);
        treasureCardPile.add(TreasureType.SYM_08);
        treasureCardPile.add(TreasureType.SYM_09);
        treasureCardPile.add(TreasureType.SYM_10);
        treasureCardPile.add(TreasureType.SYM_11);
        treasureCardPile.add(TreasureType.SYM_12);
        treasureCardPile.add(TreasureType.SYM_13);
        treasureCardPile.add(TreasureType.SYM_14);
        treasureCardPile.add(TreasureType.SYM_15);
        treasureCardPile.add(TreasureType.SYM_16);
        treasureCardPile.add(TreasureType.SYM_17);
        treasureCardPile.add(TreasureType.SYM_18);
        treasureCardPile.add(TreasureType.SYM_19);
        treasureCardPile.add(TreasureType.SYM_20);
        treasureCardPile.add(TreasureType.SYM_21);
        treasureCardPile.add(TreasureType.SYM_22);
        treasureCardPile.add(TreasureType.SYM_23);
        treasureCardPile.add(TreasureType.SYM_24);
        int anzCards = treasureCardPile.size() / players.size();
        int i = 0;
        for (Player player : players)
        {
            ArrayList<TreasureType> cardsPerPlayer = new ArrayList<TreasureType>();
            for (int j = i * anzCards; j < (i + 1) * anzCards; j++)
            {
                cardsPerPlayer.add(treasureCardPile.get(j));
            }
            player.setTreasure(cardsPerPlayer);
            ++i;
        }
    }
}
