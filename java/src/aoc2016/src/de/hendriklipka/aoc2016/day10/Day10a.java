package de.hendriklipka.aoc2016.day10;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 18:37
 */
public class Day10a
{
    private static Map<Integer,Bot> bots = new HashMap<>();

    static boolean found=false;

    public static void main(String[] args)
    {
        try
        {
            final List<String> instructions = AocDataFileUtils.getLines("2016", "day10");
            instructions.stream().filter(s->s.startsWith("bot ")).forEach(Day10a::parseBot);
            instructions.stream().filter(s -> s.startsWith("value ")).forEach(Day10a::doValue);
            // now go looking for bots with two chips
            while(!found)
            {
                for(Bot bot: bots.values())
                {
                    if (bot.chips.size()==2)
                    {
                        executeBot(bot);
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void executeBot(final Bot bot)
    {
        System.out.println("execute bot "+bot.id);
        bot.chips.sort(Integer::compareTo);
        int lower=bot.chips.get(0);
        int higher=bot.chips.get(1);
        if (lower==17 && higher==61)
        {
            found=true;
            System.out.println("bot is "+bot.id);
        }
        // when the IDs are less than 0 its an output, which we ignore for now
        if (bot.lowerBot>=0)
            bots.get(bot.lowerBot).chips.add(lower);
        if (bot.higherBot>=0)
            bots.get(bot.higherBot).chips.add(higher);
        bot.chips.clear();
    }

    private static void doValue(String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "value (\\d+) goes to bot (\\d+)");
        int value=Integer.parseInt(parts.get(0));
        int bot = Integer.parseInt(parts.get(1));
        bots.get(bot).chips.add(value);
    }

    private static void parseBot(String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "bot (\\d+) gives low to (.+) (\\d+) and high to (.+) (\\d+)");
        int botID = Integer.parseInt(parts.get(0));
        String lowerType = parts.get(1);
        int lowerID = Integer.parseInt(parts.get(2));
        String higherType = parts.get(3);
        int higherID = Integer.parseInt(parts.get(4));

        final Bot bot = new Bot();
        bot.id=botID;

        if(lowerType.equals("bot"))
            bot.lowerBot=lowerID;
        else
            bot.lowerOutput=lowerID;

        if(higherType.equals("bot"))
            bot.higherBot= higherID;
        else
            bot.higherOutput= higherID;
        bots.put(botID, bot);
    }

    private static class Bot
    {
        int id;
        int lowerBot=-1;
        int higherBot=-1;
        int lowerOutput=-1;
        int higherOutput=-1;
        List<Integer> chips = new ArrayList<>(2);

        public Integer getID()
        {
            return id;
        }
    }
}
