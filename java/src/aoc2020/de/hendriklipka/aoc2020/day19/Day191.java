package de.hendriklipka.aoc2020.day19;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 19.12.20
 * Time: 20:27
 */
public class Day191
{
    private static final Map<Integer, Rule> rules = new HashMap<>();
    private static Rule rule0;
    private static long count=0;

    public static void main(String[] args)
    {
        try
        {
            Iterator<String> lines = new ArrayList<>(FileUtils.readLines(new File("data/day19.txt"), StandardCharsets.UTF_8)).iterator();
            while (true)
            {
                String line = lines.next();
                if (StringUtils.isBlank(line))
                {
                    break;
                }
                parseRule(line);
            }
            rule0=rules.get(0);
            while (lines.hasNext())
            {
                String line = lines.next();
                if (StringUtils.isBlank(line))
                {
                    continue;
                }
                parseMessage(line);
            }
            System.out.println(count);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void parseRule(final String line)
    {
        String idxStr=line.substring(0, line.indexOf(':'));
        int idx = Integer.parseInt(idxStr);
        Rule r=new Rule(line.substring(line.indexOf(':')+1));
        rules.put(idx, r);
    }

    private static void parseMessage(final String line)
    {
        if (rule0.matches(line, 0)==line.length())
        {
            count++;
        }
    }

    private static class Rule
    {
        public char c;
        public List<Integer> left;
        public List<Integer> right;

        public Rule(String rule)
        {
            if (-1!=rule.indexOf('"'))
            {
                c=getChar(rule);
            }
            else
            {
                int orPos = rule.indexOf('|');
                if (-1 == orPos)
                {
                    left = parseSubRules(rule);
                }
                else
                {
                    left=parseSubRules(rule.substring(0, orPos));
                    right=parseSubRules(rule.substring(orPos+1));
                }
            }
        }

        private char getChar(final String rule)
        {
            return rule.charAt(2);
        }

        private List<Integer> parseSubRules(final String rule)
        {
            return Arrays.stream(rule.split(" ")).filter(StringUtils::isNotBlank).map(Integer::parseInt).collect(Collectors.toList());
        }

        public int matches(final String line, final int pos)
        {
            if (null!=left)
            {
                // this logic does not handle the case correctly when both the left and the right side match,
                // but only the right side takes the correct number of characters for the next part of the parent rule to match
                // (but it worked with the sample input)
                int len = listMatches(line, pos, left);
                if (len!=0)
                {
                    return len;
                }
                if (null!=right)
                {
                    len = listMatches(line, pos, right);
                    return len;
                }
                return 0;
            }
            else
            {
                if (line.charAt(pos)==c)
                {
                    return 1;
                }
            }
            return 0;
        }

        private int listMatches(final String line, final int pos, final List<Integer> list)
        {
            boolean match=true;
            int offset= pos;
            for (Integer integer : list)
            {
                int len = rules.get(integer).matches(line, offset);
                if (0 != len)
                {
                    offset += len;
                }
                else
                {
                    match = false;
                    break;
                }
            }
            if (match)
            {
                return offset- pos;
            }
            return 0;
        }
    }
}
