package de.hendriklipka.aoc2020.day22;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: hli
 * Date: 22.12.20
 * Time: 13:01
 */
public class Day222
{
    private static final int PLAYERS = 2;
    static List<Integer>[] players =new ArrayList[PLAYERS];
    static int totalGameNo =1;

    public static void main(String[] args)
    {
        try
        {
            parseDecks();
            playAGame(players[0], players[1], totalGameNo++);
            List<Integer> winner=null;
            if (players[0].isEmpty())
            {
                System.out.println("Player 2 wins");
                winner= players[1];
            }
            else
            {
                System.out.println("Player 1 wins");
                winner = players[0];
            }
            System.out.println(winningScore(winner));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static int playAGame(List<Integer> player1, List<Integer> player2, final int gameNo)
    {
        int round=1;
        int lastWinner=0;
        List<List<Integer>[]> gameHistory=new ArrayList<>();
        while (!player1.isEmpty() && !player2.isEmpty())
        {
            System.out.println("-- Round "+round+" (Game "+gameNo+") --");
            if (stateIsInHistory(gameHistory, player1, player2))
            {
                System.out.println("Player 1 wins to avoid infinite games");
                return 1;
            }
            List<Integer>[] newState = new ArrayList[2];
            newState[0] = new ArrayList<>(player1);
            newState[1] = new ArrayList<>(player2);
            gameHistory.add(newState);
            lastWinner=playRound(player1, player2, gameNo);
            round++;
        }
        return lastWinner;
    }

    private static boolean stateIsInHistory(final List<List<Integer>[]> history, final List<Integer> player1, final List<Integer> player2)
    {
        for (List<Integer>[] state: history)
        {
            if (player1.equals(state[0]) && player2.equals(state[1]))
                return true;
        }
        return false;
    }

    private static int playRound(final List<Integer> player1, final List<Integer> player2, final int gameNo)
    {
        System.out.println("Deck 1: "+player1);
        System.out.println("Deck 2: "+player2);
        // TODO first check for infinite game
        int card1=player1.get(0);
        int card2=player2.get(0);
        System.out.println("Player 1 plays: "+card1);
        System.out.println("Player 2 plays: "+card2);
        player1.remove(0);
        player2.remove(0);
        if (card1<=player1.size() && card2<=player2.size())
        {
            System.out.println("Playing a sub-game to determine the winner...");
            int winner=playAGame(new ArrayList(player1.subList(0, card1)), new ArrayList(player2.subList(0, card2)), totalGameNo++);
            if (winner==1)
            {
                System.out.println("Player 1 wins sub-game");
                player1.add(card1);
                player1.add(card2);
            }
            else
            {
                System.out.println("Player 2 wins sub-game");
                player2.add(card2);
                player2.add(card1);
            }
            System.out.println("now back to game "+gameNo);
            return winner;
        }
        else
        {
            if (card1>card2)
            {
                System.out.println("Player 1 wins");
                player1.add(card1);
                player1.add(card2);
                return 1;
            }
            else if (card2>card1)
            {
                System.out.println("Player 2 wins");
                player2.add(card2);
                player2.add(card1);
                return 2;
            }
            else
            {
                System.out.println("draw - what to do?");
                return 0;
            }
        }
    }

    private static void parseDecks() throws IOException
    {
        int player = 0;
        Iterator<String> lines = FileUtils.readLines(new File("data/day22.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).iterator();
        List<Integer> currentCards = null;
        while (lines.hasNext())
        {
            String line = lines.next();
            if (line.startsWith("Player"))
            {
                currentCards = new ArrayList<>();
                players[player] = currentCards;
                player++;
            }
            else
            {
                currentCards.add(Integer.parseInt(line));
            }
        }
    }

    private static long winningScore(final List<Integer> winner)
    {
        long result = 0;
        int length = winner.size();
        for (int i = 0; i < length; i++)
        {
            result += (winner.get(i) * (length - i));
        }
        return result;
    }
}
