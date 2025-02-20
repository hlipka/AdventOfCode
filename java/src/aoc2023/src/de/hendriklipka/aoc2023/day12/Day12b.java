package de.hendriklipka.aoc2023.day12;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Day12b
{
    public static void main(String[] args)
    {
        try
        {
            long sum= AocDataFileUtils.getLines("2023", "day12").stream().mapToLong(Day12b::countPossibilities).sum();
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
        long count = doCount(rule, p, groups, new HashMap<>());
        System.out.println(count);
        return count;
    }

    private static long doCount(String rule, Pattern p, List<Integer> groups, Map<String, Long> cache)
    {
        int pos = rule.indexOf('?');
        if (-1 == pos)
        {
            if (countGroups(rule, groups) != groups.size())
                return 0;
            if (p.matcher(rule).matches())
            {
                return 1;
            }
            return 0;
        }
        int countGroups = countGroups(rule.substring(0, pos), groups);
        if (countGroups > groups.size() || countGroups<0)
            return 0;

        // memoize: assume that the prefix up to here is valid, we can use the current match position (so the prefix length)
        // and the number of '#' as key. We limit this to '.' as the last char of the prefix, so we know we are not in the
        // middle of a group
        // the number of matches we can find below will never change, since groups cannot span the current boundary
        String key = null;
        if (pos>1)
        {
            char last= rule.charAt(pos - 1);
            if (last=='.')
            {
                key= pos + "-" + StringUtils.countMatches(rule.substring(0, pos), '#');
                if (cache.containsKey(key))
                {
                    return cache.get(key);
                }
            }
        }
        String str1 = (0 != pos) ? (rule.substring(0, pos) + "." + rule.substring(pos + 1)) : "." + rule.substring(pos + 1);
        String str2 = (0 != pos) ? (rule.substring(0, pos) + "#" + rule.substring(pos + 1)) : "#" + rule.substring(pos + 1);
        long count = doCount(str1, p, groups, cache) + doCount(str2, p, groups, cache);
        if (null!=key)
            cache.put(key, count);
        return count;
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
                    if (groupLen!=groups.get(currentGroup))
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
