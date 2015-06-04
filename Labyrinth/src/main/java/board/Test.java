package board;

import server.Board;

public class Test
{
    public static void main(String[] args)
    {
        LabyrinthBoard b = new LabyrinthBoard(new Board());
        System.out.println(b.toString());
        System.out.println(b.reachablePositions(0, 0));
    }
}
