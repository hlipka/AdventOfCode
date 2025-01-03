package de.hendriklipka.aoc2023.day07;

import de.hendriklipka.aoc.AocDataFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day07b
{
    private final static String CARD_RANK ="J23456789TQKA";
    public static void main(String[] args)
    {
        try
        {
            List<Hand> hands = AocDataFileUtils.getLines("2023", "day07").stream().map(Day07b::parseHand).sorted().toList();
            long score = 0;
            for (int i=1;i<=hands.size();i++)
            {
                score+=((long)i*hands.get(i-1).bid());
            }
            System.out.println(score);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Hand parseHand(String line)
    {
        String[] parts = StringUtils.split(line);
        return new Hand(parts[0], Long.parseLong(parts[1]));
    }

    private record Hand(String cardStr, long bid) implements Comparable<Hand>
    {
        @Override
        public String toString()
        {
            return "Hand{" +
                    "bid=" + bid +
                    ", cardStr='" + cardStr + '\'' +
                    ", rank='" + getType() + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Hand otherHand)
        {
            int type = getType();
            int otherType = otherHand.getType();
            if (type != otherType)
            {
                return Integer.compare(type, otherType);
            }
            for (int i = 0; i < 5; i++)
            {
                if (cardStr.charAt(i) != otherHand.cardStr.charAt(i))
                {
                    return Integer.compare(cardRank(cardStr.charAt(i)), cardRank(otherHand.cardStr.charAt(i)));
                }
            }
            return 0;
        }

        private int cardRank(char c)
        {
            return CARD_RANK.indexOf(c);
        }

        private int getType()
        {
            Map<Character, Integer> cardCounts = new HashMap<>();
            for (int i = 0; i < 5; i++)
            {
                char c = cardStr.charAt(i);
                int count = cardCounts.computeIfAbsent(c, character -> 0);
                cardCounts.put(c, count + 1);
            }
            int jCount=cardCounts.getOrDefault('J', 0);
            cardCounts.remove('J');
            List<Integer> counts = new ArrayList<>(cardCounts.values());
            counts.sort(Comparator.reverseOrder());
            if (0==jCount)
            {
                if (1 == counts.size()) // 5 of a kind
                {
                    return 6;
                }
                if (2 == counts.size())
                {
                    if (4 == counts.get(0)) // 4 of a kind
                    {
                        return 5;
                    }
                    return 4; // full house
                }
                if (3 == counts.size())
                {
                    if (3 == counts.get(0)) // 3 of a kind
                    {
                        return 3;
                    }
                    return 2; // two pairs
                }
                if (4 == counts.size()) // 2 of a kind
                {
                    return 1;
                }
                return 0; // highest card
            }
            if (counts.isEmpty()) // no other cards
            {
                return 6;
            }
            if (1 == counts.size()) // 5 of a kind
            {
                return 6;
            }
            if (4 == counts.get(0)+jCount) // 4 of a kind
            {
                return 5;
            }
            if (3 == counts.get(0)+jCount &&   2==counts.get(1)) // full house
            {
                return 4;
            }
            if (3 == counts.get(0)+jCount) // 3 of a kind
            {
                return 3;
            }
            // a pair. We cannot have 'two pairs' with a joker - it would be better to make '3 of a kind' from it
            if (2 == counts.get(0)+jCount)
            {
                return 1;
            }
            return 0; // highest card
        }
    }
}
