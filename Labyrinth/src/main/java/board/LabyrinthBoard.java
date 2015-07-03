package board;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import jaxb.BoardType;
import jaxb.CardType;
import jaxb.CardType.Openings;
import jaxb.TreasureType;

/**
 * A representation of the current board with more convenient methods than the BoardType received from the server. A
 * instance of this class mainly consists of a 2d-int-array representing the cards openings and two Sets representing
 * the players' and treasures' positions.
 * 
 * @author Stefan Klug
 */
public class LabyrinthBoard
{
    public static final int ROW_COUNT = 7;
    public static final int COLUMN_COUNT = 7;

    public static final int INDEX_UP = 0;
    public static final int INDEX_RIGHT = 1;
    public static final int INDEX_DOWN = 2;
    public static final int INDEX_LEFT = 3;

    private int[][] cards;
    private Point forbidden;
    private HashSet<Integer> shiftCards;
    private HashMap<Integer, Point> playerPositions;
    private HashMap<TreasureType, Point> treasurePositions;

    /**
     * A LabyrinthBoard can be created from the board received from the server
     */
    public LabyrinthBoard(BoardType board)
    {
        this.cards = new int[ROW_COUNT][COLUMN_COUNT];
        this.playerPositions = new HashMap<Integer, Point>();
        this.treasurePositions = new HashMap<TreasureType, Point>();
        for (int row = 0; row < ROW_COUNT; row++)
        {
            for (int column = 0; column < COLUMN_COUNT; column++)
            {
                CardType card = board.getRow().get(row).getCol().get(column);
                this.cards[row][column] = cardToInt(card);
                List<Integer> playerIDs = card.getPin().getPlayerID();
                if (playerIDs != null)
                {
                    for (Integer playerID : playerIDs)
                    {
                        playerPositions.put(playerID, new Point(row, column));
                    }
                }
                TreasureType treasure = card.getTreasure();
                if (treasure != null)
                {
                    this.treasurePositions.put(treasure, new Point(row, column));
                }
            }
        }
        this.shiftCards = new HashSet<Integer>();
        int card = cardToInt(board.getShiftCard());
        this.shiftCards.add(card);
        for (int i = 0; i < 3; i++)
        {
            card = rotateRight(card);
            this.shiftCards.add(card);
        }
        this.forbidden = board.getForbidden() == null ? null
                : new Point(board.getForbidden().getRow(), board.getForbidden().getCol());
    }

    /**
     * @return true, if you can move directly from (row,column) to (row+1,column); otherwise false
     */
    public boolean canGoDown(int row, int column)
    {
        return isCorrectIndex(row, column) && row != ROW_COUNT - 1 && isBitSet(this.cards[row][column], INDEX_DOWN) && isBitSet(this.cards[row + 1][column], INDEX_UP);
    }

    /**
     * x=row, y=column
     * 
     * @return true, if you can move directly from (row,column) to (row+1,column); otherwise false
     */
    public boolean canGoDown(Point p)
    {
        return canGoDown(p.x, p.y);
    }

    /**
     * @return true, if you can move directly from (row,column) to (row,column-1); otherwise false
     */
    public boolean canGoLeft(int row, int column)
    {
        return isCorrectIndex(row, column) && column != 0 && isBitSet(this.cards[row][column], INDEX_LEFT) && isBitSet(this.cards[row][column - 1], INDEX_RIGHT);
    }

    /**
     * x=row, y=column
     * 
     * @return true, if you can move directly from (row,column) to (row,column-1); otherwise false
     */
    public boolean canGoLeft(Point p)
    {
        return canGoLeft(p.x, p.y);
    }

    /**
     * @return true, if you can move directly from (row,column) to (row,column+1); otherwise false
     */
    public boolean canGoRight(int x, int y)
    {
        return isCorrectIndex(x, y) && y != COLUMN_COUNT - 1 && isBitSet(this.cards[x][y], INDEX_RIGHT) && isBitSet(this.cards[x][y + 1], INDEX_LEFT);
    }

    /**
     * x=row, y=column
     * 
     * @return true, if you can move directly from (row,column) to (row,column+1); otherwise false
     */
    public boolean canGoRight(Point p)
    {
        return canGoRight(p.x, p.y);
    }

    /**
     * @return true, if you can move directly from (row,column) to (row-1,column); otherwise false
     */
    public boolean canGoUp(int row, int column)
    {
        return isCorrectIndex(row, column) && row != 0 && isBitSet(this.cards[row][column], INDEX_UP) && isBitSet(this.cards[row - 1][column], INDEX_DOWN);
    }

    /**
     * x=row, y=column
     * 
     * @return true, if you can move directly from (row,column) to (row-1,column); otherwise false
     */
    public boolean canGoUp(Point p)
    {
        return canGoUp(p.x, p.y);
    }

    /**
     * @return the position, where the shift card must not be inserted (x=row, y=column)
     */
    public Point getForbidden()
    {
        return new Point(this.forbidden);
    }

    /**
     * @return the player's position on the board (x=row, y=column)
     */
    public Point getPosition(int playerID)
    {
        Point pos = this.playerPositions.get(playerID);
        return pos == null ? pos : new Point(pos);
    }

    /**
     * @return the treasure's position on the board (x=row, y=column)
     */
    public Point getPosition(TreasureType treasure)
    {
        Point pos = this.treasurePositions.get(treasure);
        return pos == null ? null : new Point(pos);
    }

    /**
     * @return all possible values for the shift card (min. 2, max. 4)
     */
    public Set<Integer> getShiftCards()
    {
        return new HashSet<Integer>(shiftCards);
    }

    /**
     * @return all positions, which can be reached from (x,y); (x,y) is always present in the returned Set
     */
    public Set<Point> reachablePositions(int x, int y)
    {
        Set<Point> ret = new HashSet<Point>();
        Queue<Point> openList = new LinkedList<Point>();
        openList.add(new Point(x, y));
        while (!openList.isEmpty())
        {
            Point p = openList.remove();
            ret.add(p);
            // check if the neighbored fields can be reached
            if (canGoDown(p))
            {
                Point next = new Point(p.x + 1, p.y);
                if (!ret.contains(next) && !openList.contains(next))
                {
                    openList.add(next);
                }
            }
            if (canGoLeft(p))
            {
                Point next = new Point(p.x, p.y - 1);
                if (!ret.contains(next) && !openList.contains(next))
                {
                    openList.add(next);
                }
            }
            if (canGoRight(p))
            {
                Point next = new Point(p.x, p.y + 1);
                if (!ret.contains(next) && !openList.contains(next))
                {
                    openList.add(next);
                }
            }
            if (canGoUp(p))
            {
                Point next = new Point(p.x - 1, p.y);
                if (!ret.contains(next) && !openList.contains(next))
                {
                    openList.add(next);
                }
            }
        }
        return ret;
    }

    /**
     * @return all positions, which can be reached from p; if p isn't null, it is present in the returned Set
     */
    public Set<Point> reachablePositions(Point p)
    {
        return p == null ? new HashSet<Point>() : reachablePositions(p.x, p.y);
    }

    public int toInt(Point pos)
    {
        return this.cards[pos.x][pos.y];
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("|-----|------|------|------|------|------|------|\n");
        for (int row = 0; row < ROW_COUNT; row++)
        {
            sb.append('|');
            for (int column = 0; column < COLUMN_COUNT; column++)
            {
                sb.append(canGoLeft(row, column) ? 'L' : ' ');
                sb.append(canGoUp(row, column) ? 'U' : ' ');
                sb.append(canGoDown(row, column) ? 'D' : ' ');
                sb.append(canGoRight(row, column) ? 'R' : ' ');
                sb.append(" | ");
            }
            sb.append("\n|-----|------|------|------|------|------|------|\n");
        }
        return sb.toString();
    }

    private int cardToInt(CardType card)
    {
        Openings openings = card.getOpenings();
        int value = 0;
        if (openings.isTop())
        {
            value |= 1 << INDEX_UP;
        }
        if (openings.isRight())
        {
            value |= 1 << INDEX_RIGHT;
        }
        if (openings.isBottom())
        {
            value |= 1 << INDEX_DOWN;
        }
        if (openings.isLeft())
        {
            value |= 1 << INDEX_LEFT;
        }
        return value;
    }

    public boolean isBitSet(int card, int index)
    {
        return (card & (1 << index)) != 0;
    }

    private boolean isCorrectIndex(int x, int y)
    {
        return x >= 0 && x < ROW_COUNT && y >= 0 && y < COLUMN_COUNT;
    }

    private int rotateRight(int card)
    {
        int value = 0;
        if (isBitSet(card, INDEX_LEFT))
        {
            value |= 1 << INDEX_UP;
        }
        if (isBitSet(card, INDEX_UP))
        {
            value |= 1 << INDEX_RIGHT;
        }
        if (isBitSet(card, INDEX_RIGHT))
        {
            value |= 1 << INDEX_DOWN;
        }
        if (isBitSet(card, INDEX_DOWN))
        {
            value |= 1 << INDEX_LEFT;
        }
        return value;
    }
}
