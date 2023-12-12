package de.hendriklipka.aoc2023.day12;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Day12c
{
    public static void main(String[] args)
    {
        try
        {
            long sum=AocParseUtils.getLines("2023", "day12").stream().parallel().mapToLong(Day12c::countPossibilities).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long countPossibilities(String line)
    {
        String rule = AocParseUtils.parseStringFromString(line, "([.#\\?]+) .*");
        rule=rule+"?"+rule+"?"+rule+"?"+rule+"?"+rule;
        System.out.println("real rule="+rule);
        List<Integer> groups1 = Arrays.stream(AocParseUtils.parseStringFromString(line, "[.#\\?]+ ([0-9,]+)").split(",")).map(Integer::parseInt).toList();
        List<Integer> groups = new ArrayList<>();
        groups.addAll(groups1);
        groups.addAll(groups1);
        groups.addAll(groups1);
        groups.addAll(groups1);
        groups.addAll(groups1);
        System.out.println("groups="+groups);
        String pattern = "^\\.*";
        for (int i = 0; i < groups.size(); i++)
        {
            int len = groups.get(i);
            pattern += "(#{" + len + "})";
            if (i != groups.size() - 1)
            {
                pattern += "\\.+";
            }
        }
        pattern += "\\.*$";
        Pattern p = Pattern.compile(pattern);
        System.out.println("pattern for " + line + " is " + pattern);
        long count = doCount(rule, p, groups);
        System.out.println(count);
        return count;
    }

    private static long doCount(String rule, Pattern p, List<Integer> groups)
    {
        int pos = rule.indexOf('?');
        if (-1 == pos)
        {
            if (countGroups(rule, groups) != groups.size())
                return 0;
            if (p.matcher(rule).matches())
            {
//                System.out.println("found "+rule);
                return 1;
            }
            return 0;
        }
        int countGroups = countGroups(rule.substring(0, pos), groups);
        if (countGroups > groups.size() || countGroups<0)
            return 0;
        String str1 = (0 != pos) ? (rule.substring(0, pos) + "." + rule.substring(pos + 1)) : "." + rule.substring(pos + 1);
        String str2 = (0 != pos) ? (rule.substring(0, pos) + "#" + rule.substring(pos + 1)) : "#" + rule.substring(pos + 1);
        return doCount(str1, p, groups) + doCount(str2, p, groups);
    }

    private static int countGroups(String rule, List<Integer> groups)
    {
        int groupCount=groups.size();
        int groupsFound=0;
        int currentGroup=-1;
        int groupLen=0;
        boolean inGroup=false;
        char[] chars=rule.toCharArray();
        for (char c : chars)
        {
            if (c == '#')
            {
                if (!inGroup)
                {
                    // start a new group
                    currentGroup++;
                    if (currentGroup==groupCount)
                    {
                        return -1;
                    }
                    groupLen = 1;
                    inGroup = true;
                    groupsFound++;
                }
                else
                {
                    groupLen++;
                }
            }
            else
            {
                if (inGroup)
                {
                    inGroup = false;
                    if (currentGroup>= groups.size() || groupLen!=groups.get(currentGroup))
                    {
                        return -1;
                    }
                }
            }
        }
        // when we ended with a '#', we reject this only when the group would have been too long
        // otherwise further work could make the current state still valid
        if (inGroup && groupLen>groups.get(currentGroup))
        {
            return -1;
        }
        return groupsFound;
    }
}
