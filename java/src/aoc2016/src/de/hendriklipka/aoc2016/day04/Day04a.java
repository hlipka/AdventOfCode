package de.hendriklipka.aoc2016.day04;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 16:28
 */
public class Day04a
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> rooms = AocDataFileUtils.getLines("2016", "day04");
            int total=0;
            for (String room: rooms)
            {
                System.out.println(room);
                final List<String> parts = AocParseUtils.parsePartsFromString(room, "(([a-z]+\\-)*)(\\d+)\\[([a-z]+)\\]");
                String name=parts.get(0);
                final String[] names = name.split("-");
                String sector=parts.get(2);
                String checksum=parts.get(3);
                if (isValidRoom(names, checksum))
                {
                    total+=Integer.parseInt(sector);
                }
            }
            System.out.println(total);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidRoom(final String[] names, final String checksum)
    {
        Map<String, Integer> chars = countChars(names);
        List<String> sorted = chars.entrySet().stream().sorted(new CharComp()).map(Map.Entry::getKey).limit(checksum.length()).toList();
        final String check = StringUtils.join(sorted,"");
        return check.equals(checksum);
    }

    private static Map<String, Integer> countChars(final String[] names)
    {
        final Map<String, Integer> chars = new HashMap<>();
        for (String name: names)
        {
            for (char c: name.toCharArray())
            {
                Integer i=chars.getOrDefault(Character.toString(c), 0);
                chars.put(Character.toString(c), i+1);
            }
        }
        return chars;
    }

    private static class CharComp implements java.util.Comparator<Map.Entry<String, Integer>>
    {
        @Override
        public int compare(final Map.Entry<String, Integer> e1, final Map.Entry<String, Integer> e2)
        {
            if (e1.getValue()>e2.getValue())
            {
                return -1;
            }
            if (e1.getValue() < e2.getValue())
            {
                return 1;
            }
            return Character.compare(e1.getKey().charAt(0), e2.getKey().charAt(0));
        }
    }
}
