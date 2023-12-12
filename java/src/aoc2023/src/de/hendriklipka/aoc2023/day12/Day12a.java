package de.hendriklipka.aoc2023.day12;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Day12a
{
    public static void main(String[] args)
    {
        try
        {
            int sum=AocParseUtils.getLines("2023", "day12").stream().mapToInt(Day12a::countPossibilities).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int countPossibilities(String line)
    {
        String rule = AocParseUtils.parseStringFromString(line, "([.#\\?]+) .*");
        List<Integer> groups = Arrays.stream(AocParseUtils.parseStringFromString(line, "[.#\\?]+ ([0-9,]+)").split(",")).map(Integer::parseInt).toList();
        String pattern = "\\.*";
        for (int i = 0; i < groups.size(); i++)
        {
            int len = groups.get(i);
            pattern += "(#{" + len + "})";
            if (i != groups.size() - 1)
            {
                pattern += "\\.+";
            }
        }
        pattern += "\\.*";
        Pattern p = Pattern.compile(pattern);
        System.out.println("pattern for " + line + " is " + pattern);
        int count = doCount(rule, p);
        System.out.println(count);
        return count;
    }

    private static int doCount(String rule, Pattern p)
    {
        int pos = rule.indexOf('?');
        if (-1 == pos)
        {
//            System.out.println("test "+rule);
            if (p.matcher(rule).matches())
            {
                return 1;
            }
            return 0;
        }
        String str1 = (0 != pos) ? (rule.substring(0, pos) + "." + rule.substring(pos + 1)) : "." + rule.substring(pos + 1);
        String str2 = (0 != pos) ? (rule.substring(0, pos) + "#" + rule.substring(pos + 1)) : "#" + rule.substring(pos + 1);
        return doCount(str1, p) + doCount(str2, p);
    }
}
