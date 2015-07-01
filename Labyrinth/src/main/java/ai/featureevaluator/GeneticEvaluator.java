package ai.featureevaluator;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jaxb.AwaitMoveMessageType;
import jaxb.BoardType;
import jaxb.CardType;
import jaxb.TreasureType;
import jaxb.TreasuresToGoType;
import util.CurrentID;
import util.Loggers;
import ai.Evaluator;
import board.LabyrinthBoard;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import competition.EvaluatorCompetition;

public class GeneticEvaluator extends Thread implements Evaluator, Serializable
{
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException
    {
        try
        {
            BigInteger i = BigInteger.ZERO;
            EvaluatorCompetition<GeneticEvaluator> competition = new EvaluatorCompetition<>(Arrays.asList(new GeneticEvaluator[] { args.length > 0
                    ? new GeneticEvaluator(new ObjectInputStream(new FileInputStream((i = new BigInteger(args[0])) + ".bin")))
                    : new GeneticEvaluator(), new GeneticEvaluator(), new GeneticEvaluator(), new GeneticEvaluator() }));
            while (true)
            {
                boolean cancelled = true;
                long zeit = System.nanoTime();
                GeneticEvaluator winner;
                try
                {
                    winner = competition.runCompetitionInNewThread().get(60, TimeUnit.SECONDS);
                    cancelled = false;
                }
                catch (TimeoutException e)
                {
                    int bestTreasureCounter = Integer.MIN_VALUE;
                    List<GeneticEvaluator> evaluators = competition.getEvaluators();
                    winner = evaluators.get(0);
                    ArrayList<GeneticEvaluator> possibleWinners = new ArrayList<GeneticEvaluator>();
                    for (GeneticEvaluator evaluator : evaluators)
                    {
                        if (evaluator.u.treasures >= bestTreasureCounter)
                        {
                            if (evaluator.u.treasures > bestTreasureCounter)
                            {
                                bestTreasureCounter = evaluator.u.treasures;
                                possibleWinners.clear();
                            }
                            possibleWinners.add(evaluator);
                        }
                    }
                    int bestDistanceCounter = Integer.MAX_VALUE;
                    for (GeneticEvaluator possibleWinner : possibleWinners)
                    {
                        if (possibleWinner.u.distance < bestDistanceCounter)
                        {
                            bestDistanceCounter = possibleWinner.u.distance;
                            winner = possibleWinner;
                        }
                    }
                }
                zeit = System.nanoTime() - zeit;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                winner.save(new ObjectOutputStream(byteArrayOutputStream));
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                Files.copy(is, Paths.get(i + ".bin"), StandardCopyOption.REPLACE_EXISTING);
                Loggers.EVOLUTION.info((cancelled
                        ? "The winner of generation " + i + " collected " + winner.u.treasures + " " + (winner.u.treasures == 1
                                ? "treasure" : "treasures") + "."
                        : "Generation " + i + " took " + 0.000_000_001 * zeit + " seconds.") + " The winner's brain's size is " + winner.complexity() + ".");
                Set<GeneticEvaluator> nextGeneration = new HashSet<>();
                nextGeneration.add(winner);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                for (int j = (int) (Math.random() * 4.); j > 0; j--)
                {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                    GeneticEvaluator child = new GeneticEvaluator(new ObjectInputStream(byteArrayInputStream));
                    byteArrayInputStream.close();
                    child.mutate();
                    nextGeneration.add(child);
                }
                competition.replaceAllEvaluators(nextGeneration);
                i = i.add(BigInteger.ONE);
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println(new GeneticEvaluator(new ObjectInputStream(new BufferedInputStream(new FileInputStream(args[0] + ".bin")))));
        }
    }

    private synchronized char i(AwaitMoveMessageType awaitMoveMessageType, BoardType possibleBoardType, CurrentID currentID, char x)
    {
        if (x <= 7 * 7)
        {
            CardType vision = x == 0 ? possibleBoardType.getShiftCard()
                    : possibleBoardType.getRow().get(x / 7).getCol().get(x % 7);
            return (char) ((vision.getOpenings().isTop() ? 0b1000000000000000 : 0b0000000000000000) | (vision.getOpenings().isBottom()
                    ? 0b0100000000000000 : 0b0000000000000000) | (vision.getOpenings().isLeft() ? 0b0010000000000000
                    : 0b0000000000000000) | (vision.getOpenings().isRight() ? 0b0001000000000000 : 0b0000000000000000) | (vision.getTreasure() == null
                    ? 0b0000111111111111 : vision.getTreasure().ordinal() + 1));
        }
        if (x > Character.MAX_VALUE - (char) 4)
        {
            x *= -1;
            for (int i = 0; i < 7; ++i)
            {
                for (int k = 0; k < 7; ++k)
                {
                    if (possibleBoardType.getRow().get(i).getCol().get(k).getPin().getPlayerID().contains(new Integer(x)))
                    {
                        int treasures = 0b11111111;
                        for (TreasuresToGoType treasureToGoType : awaitMoveMessageType.getTreasuresToGo())
                        {
                            if (treasureToGoType.getPlayer() == x)
                            {
                                treasures = treasureToGoType.getTreasures();
                                break;
                            }
                        }
                        return (char) (i << 12 | k << 8 | treasures);
                    }
                }
            }
            return (char) -1;
        }
        return (char) (x < 0b1000000000000000 ? awaitMoveMessageType.getTreasure().ordinal() + 1
                : currentID.getCurrentID());
    }

    private char o(char x) throws IOException
    {
        u.score = x;
        throw new IOException();
    }

    /** Der Codeschnipsel */
    private ArrayList<GeneticEvaluator> genetics = new ArrayList<GeneticEvaluator>();
    /** Der Typ des Codeschnipsels */
    private char type = ' ';
    /** Eine innere Zahlenliste */
    public U u = new U();
    AwaitMoveMessageType awaitMoveMessageType;
    BoardType possibleBoardType;
    CurrentID currentID;

    /**
     * Ruft eine Mutation hervor.
     */
    public synchronized void mutate()
    {
        do
        {
            switch (type)
            {
            case ' ':
                if (genetics.isEmpty())
                {
                    if (Math.random() < 0.5)
                    {
                        genetics.add((int) (Math.random() * genetics.size()), new GeneticEvaluator(';'));
                    }
                }
                else
                {
                    if (Math.random() < 0.5)
                    {
                        genetics.remove((int) (Math.random() * genetics.size()));
                    }
                    else if (Math.random() < 0.5)
                    {
                        genetics.add((int) (Math.random() * genetics.size()), new GeneticEvaluator(';'));
                    }
                    else
                    {
                        genetics.get((int) (Math.random() * genetics.size())).mutate();
                    }
                }
                break;
            case '#':
                String s = Integer.toBinaryString(genetics.get(0).type).substring(16);
                int i = (int) (Math.random() * 16.);
                genetics.get(0).type = (char) Short.parseShort(s.substring(0, i) + (s.charAt(i) == '0' ? '1' : '0') + s.substring(i + 1), 2);
                break;
            case '?':
                if (Math.random() < 0.5)
                {
                    genetics.set(0, new GeneticEvaluator('°'));
                }
                else
                {
                    genetics.get((int) (Math.random() * 2.) + 1).mutate();
                }
                break;
            case ':':
                if (Math.random() < 0.5)
                {
                    genetics.set(0, new GeneticEvaluator('°'));
                }
                else
                {
                    genetics.get(1).mutate();
                }
                break;

            default:
                if (!genetics.isEmpty())
                {
                    genetics.set((int) (Math.random() * genetics.size()), new GeneticEvaluator('°'));
                }
                break;
            }
        }
        while (Math.random() < 0.5);
    }

    public synchronized int complexity()
    {
        int complexity = 1;
        if (type != '#')
        {
            for (GeneticEvaluator genetic : genetics)
            {
                complexity += genetic.complexity();
            }
        }
        return complexity;
    }

    public GeneticEvaluator(ArrayList<GeneticEvaluator> genetics, AwaitMoveMessageType awaitMoveMessageType, BoardType possibleBoardType, CurrentID currentID, U u)
    {
        this.genetics = genetics;
        this.awaitMoveMessageType = awaitMoveMessageType;
        this.possibleBoardType = possibleBoardType;
        this.currentID = currentID;
        this.u = u;
    }

    /**
     * Erzeugt ein Beispielprogramm.
     */
    public GeneticEvaluator()
    {
        this(50);
    }

    /**
     * Erzeugt das Hauptprogramm.
     */
    public GeneticEvaluator(int complexity)
    {
        while (complexity() < complexity)
        {
            genetics.add(new GeneticEvaluator(';'));
        }
    }

    /**
     * Erzeugt einen Codeschnipsel vom angegebenen Typ.
     * 
     * @param character
     *            Das Leerzeichen steht für einen beliebigen Codeschnipsel. <code>;</code> steht für einen beliebigen
     *            Dienst, <code>°</code> für eine beliebige Abfrage. <code>#</code> steht für eine beliebige Zahl.
     *            Die Grundrechenarten sind selbsterklärend. <code>~</code> steht für Gleichheit und <code>\</code>
     *            steht für Ungleichheit. <code>?</code> steht für if-else und <code>:</code> seht für while.
     *            <code>_</code> steht für den Lesevorgang aus <code>u</code> und <code>=</code> steht für den
     *            Schreibvorgang in <code>u</code>. <code>i</code> steht für Input und <code>o</code> steht für Output.
     *            Diese Funktionen akzeptieren eine einzige Zahl und können für alles benutzt werden. <code>i</code> und
     *            <code>o</code> sind Funktionen, die selbst geschrieben werden müssen.
     */
    public GeneticEvaluator(char type)
    {
        this.type = type == ';' ? "?:=ooo".toCharArray()[(int) (Math.random() * 6)] : type == '°'
                ? "###############+-*/%^<>&|~\\!_i".toCharArray()[(int) (Math.random() * 30)] : type;
        switch (this.type)
        {
        case ' ':
            genetics.add(new GeneticEvaluator(';'));
            break;
        case '#':
            genetics.add(new GeneticEvaluator((char) (Math.random() * ((int) Character.MAX_VALUE + 1))));
            break;
        case '+':
        case '-':
        case '*':
        case '/':
        case '%':
        case '^':
        case '<':
        case '>':
        case '&':
        case '|':
        case '~':
        case '\\':
        case '=':
            genetics.add(new GeneticEvaluator('°'));
        case '!':
        case '_':
        case 'i':
        case 'o':
            genetics.add(new GeneticEvaluator('°'));
            break;
        case '?':
            genetics.add(new GeneticEvaluator('°'));
            genetics.add(new GeneticEvaluator(' '));
            genetics.add(new GeneticEvaluator(' '));
            break;
        case ':':
            genetics.add(new GeneticEvaluator('°'));
            genetics.add(new GeneticEvaluator(' '));
            break;

        default:
            break;
        }
    }

    @SuppressWarnings("unchecked")
    public GeneticEvaluator(ObjectInputStream in) throws ClassNotFoundException, IOException
    {
        type = (char) in.readObject();
        genetics = (ArrayList<GeneticEvaluator>) in.readObject();
        in.close();
    }

    public char getType()
    {
        return type;
    }

    public void setType(char type)
    {
        this.type = type;
    }

    public ArrayList<GeneticEvaluator> getGeneticFeatures()
    {
        return genetics;
    }

    public void setGeneticFeatures(ArrayList<GeneticEvaluator> genetics)
    {
        this.genetics = genetics;
    }

    public void save(ObjectOutputStream out) throws IOException
    {
        out.writeObject(type);
        out.writeObject(genetics);
        out.close();
    }

    @Override
    public synchronized String toString()
    {
        switch (type)
        {
        case ' ':
            StringBuilder string = new StringBuilder("{");
            for (GeneticEvaluator genetic : genetics)
            {
                string.append(genetic);
            }
            string.append("}");
            return string.toString();
        case '#':
            return "(char)" + Integer.toString(genetics.get(0).type);
        case '+':
        case '-':
        case '*':
            return "(char)(" + genetics.get(0) + type + genetics.get(1) + ")";
        case '/':
            return "div(" + genetics.get(0) + "," + genetics.get(1) + ")";
        case '%':
            return "mod(" + genetics.get(0) + "," + genetics.get(1) + ")";
        case '^':
            return "(char)Math.pow(" + genetics.get(0) + "," + genetics.get(1) + ")";
        case '<':
        case '>':
            return "((" + genetics.get(0) + Character.toString(type) + genetics.get(1) + ")?(char)1:(char)0)";
        case '&':
            return "((" + genetics.get(0) + "==(char)0)||(" + genetics.get(1) + "==(char)0)?(char)0:(char)1)";
        case '|':
            return "((" + genetics.get(0) + "==(char)0)&&(" + genetics.get(1) + "==(char)0)?(char)0:(char)1)";
        case '~':
            return "((" + genetics.get(0) + "==" + genetics.get(1) + ")?(char)1:(char)0)";
        case '\\':
            return "((" + genetics.get(0) + "==" + genetics.get(1) + ")?(char)0:(char)1)";
        case '!':
            return "((" + genetics.get(0) + "==(char)0)?(char)1:(char)0)";
        case '?':
            return "if(" + genetics.get(0) + "==(char)0)" + genetics.get(2) + "else" + genetics.get(1);
        case ':':
            return "while(" + genetics.get(0) + "!=(char)0)" + genetics.get(1);
        case '_':
            return "u.get(" + genetics.get(0) + ")";
        case '=':
            return "u.set(" + genetics.get(0) + "," + genetics.get(1) + ");";
        case 'i':
            return "i(" + genetics.get(0) + ")";
        case 'o':
            return "o(" + genetics.get(0) + ");";

        default:
            return type + "\n";
        }
    }

    @Override
    public void run()
    {
        u.treasures = 0;
        u.distance = Integer.MAX_VALUE;
        u.score = Character.MAX_VALUE;
        try
        {
            while (!isInterrupted())
            {
                eval(awaitMoveMessageType, possibleBoardType, currentID, u);
            }
        }
        catch (IOException e)
        {}
    }

    private char eval(AwaitMoveMessageType awaitMoveMessageType, BoardType possibleBoardType, CurrentID currentID, U u) throws IOException
    {
        switch (type)
        {
        case '#':
            return genetics.get(0).type;
        case '+':
            return (char) (genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) + genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '-':
            return (char) (genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) - genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '*':
            return (char) (genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) * genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '/':
            return div(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u), genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '%':
            return mod(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u), genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '^':
            return (char) Math.pow(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u), genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '<':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) < genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u)
                    ? (char) 1 : (char) 0;
        case '>':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) > genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u)
                    ? (char) 1 : (char) 0;
        case '&':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == (char) 0 || genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == (char) 0
                    ? (char) 0 : (char) 1;
        case '|':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == (char) 0 && genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == (char) 0
                    ? (char) 0 : (char) 1;
        case '~':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u)
                    ? (char) 1 : (char) 0;
        case '\\':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u)
                    ? (char) 0 : (char) 1;
        case '!':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == (char) 0 ? (char) 1
                    : (char) 0;
        case '?':
            return genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == 0
                    ? genetics.get(2).eval(awaitMoveMessageType, possibleBoardType, currentID, u)
                    : genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u);
        case ':':
            char result = (char) 0;
            while (!(isInterrupted() || genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u) == 0))
            {
                result = genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u);
            }
            return result;
        case '_':
            return u.get(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case '=':
            return u.set(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u), genetics.get(1).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case 'i':
            return i(awaitMoveMessageType, possibleBoardType, currentID, genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u));
        case 'o':
            return o(genetics.get(0).eval(awaitMoveMessageType, possibleBoardType, currentID, u));

        default:
            char i = (char) 0;
            for (GeneticEvaluator genetic : genetics)
            {
                if (isInterrupted())
                {
                    return i;
                }
                else
                {
                    i = genetic.eval(awaitMoveMessageType, possibleBoardType, currentID, u);
                }
            }
            return i;
        }
    }

    private char div(char dividend, char divisor)
    {
        return divisor == (char) 0 ? (char) Short.MIN_VALUE : (char) (dividend / divisor);
    }

    private char mod(char dividend, char divisor)
    {
        return divisor == (char) 0 ? (char) 0 : (char) (dividend % divisor);
    }

    @Override
    public BoardType findBest(AwaitMoveMessageType awaitMoveMessageType, ImmutableSet<BoardType> possibleBoardTypes, CurrentID currentID)
    {
        ArrayList<U> us = new ArrayList<U>();
        ArrayList<GeneticEvaluator> evaluators = new ArrayList<GeneticEvaluator>();
        ImmutableList<BoardType> boardTypes = possibleBoardTypes.asList();
        for (BoardType boardType : boardTypes)
        {
            us.add(u == null ? new U() : (U) u.clone());
            evaluators.add(new GeneticEvaluator(genetics, awaitMoveMessageType, boardType, currentID, us.get(us.size() - 1)));
            evaluators.get(evaluators.size() - 1).start();
        }
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {}
        char bestScore = Character.MAX_VALUE;
        BoardType bestBoard = null;
        for (int i = 0; i < evaluators.size(); ++i)
        {
            evaluators.get(i).interrupt();
            char score = us.get(i).score;
            if (bestScore >= score)
            {
                bestBoard = boardTypes.get(i);
                bestScore = score;
                u = us.get(i);
            }
        }
        u.distance = new DistanceToTreasure().measure(awaitMoveMessageType, bestBoard, currentID);
        if (u.distance == 0)
        {
            u.treasures++;
        }

        int id = currentID.getCurrentID();
        TreasureType treasure = awaitMoveMessageType.getTreasure();
        int possibleMoves = possibleBoardTypes.size();
        System.out.println("Spieler-ID: " + id);
        System.out.println("moegliche Zuege: " + possibleMoves);

        LabyrinthBoard before = new LabyrinthBoard(awaitMoveMessageType.getBoard());
        LabyrinthBoard after = new LabyrinthBoard(bestBoard);

        Point pos1 = before.getPosition(id);
        Point pos2 = after.getPosition(id);
        System.out.println("Spieler vorher:  Reihe=" + pos1.x + " Spalte=" + pos1.y);
        System.out.println("Spieler nachher: Reihe=" + pos2.x + " Spalte=" + pos2.y);

        pos1 = before.getPosition(treasure);
        pos2 = after.getPosition(treasure);
        System.out.println("Schatz vorher:   Reihe=" + pos1.x + " Spalte=" + pos1.y);
        System.out.println("Schatz nachher:  Reihe=" + pos2.x + " Spalte=" + pos2.y + "\n");

        return bestBoard;
    }
}
