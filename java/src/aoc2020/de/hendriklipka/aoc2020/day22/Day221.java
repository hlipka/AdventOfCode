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
public class Day221
{
    private static final int PLAYERS = 2;
    static List<Integer>[] players =new ArrayList[PLAYERS];

    public static void main(String[] args)
    {
        try
        {
            int player=0;
            Iterator<String> lines = FileUtils.readLines(new File("data/day22.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).iterator();
            List<Integer> currentCards=null;
            while (lines.hasNext())
            {
                String line=lines.next();
                if (line.startsWith("Player"))
                {
                    currentCards = new ArrayList<>();
                    players[player]=currentCards;
                    player++;
                }
                else
                {
                    currentCards.add(Integer.parseInt(line));
                }
            }
            long rounds=0L;
            while (!players[0].isEmpty() && !players[1].isEmpty())
            {
                playRound(players[0], players[1]);
                rounds++;
            }
            System.out.println(rounds);
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

    private static long winningScore(final List<Integer> winner)
    {
        long result=0;
        int length=winner.size();
        for (int i=0;i<length;i++)
        {
            result+=(winner.get(i)*(length-i));
        }
        return result;
    }

    private static void playRound(final List<Integer> player1, final List<Integer> player2)
    {
        int card1=player1.get(0);
        int card2=player2.get(0);
        player1.remove(0);
        player2.remove(0);
        if (card1>card2)
        {
            player1.add(card1);
            player1.add(card2);
        }
        else if (card2>card1)
        {
            player2.add(card2);
            player2.add(card1);
        }
        else
        {
            System.out.println("draw - what to do?");
        }
    }
}
