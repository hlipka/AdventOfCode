package de.hendriklipka.aoc2020.day19;

import org.apache.commons.collections4.ListUtils;
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
public class Day192
{
    private static final Map<Integer, Rule> rules = new HashMap<>();
    private static long count=0;

    public static void main(String[] args)
    {
        try
        {
            // use a different files as the rule definitions have changed slightly
            Iterator<String> lines = new ArrayList<>(FileUtils.readLines(new File("../data/2020/day19a.txt"), StandardCharsets.UTF_8)).iterator();
            while (true)
            {
                String line = lines.next();
                if (StringUtils.isBlank(line))
                {
                    break;
                }
                parseRule(line);
            }
            Rule rule0=rules.get(0);
            List<Integer> empty=new ArrayList<>();
            while (lines.hasNext())
            {
                String line = lines.next();
                if (StringUtils.isBlank(line))
                {
                    continue;
                }
                if (rule0.matches(line, empty))
                {
                    count++;
                }
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
        Rule r=new Rule(idx, line.substring(line.indexOf(':')+1));
        rules.put(idx, r);
    }

    private static class Rule
    {
        private final int _id;
        public char c;
        public List<Integer> left;
        public List<Integer> right;

        public Rule(int id, String rule)
        {
            _id=id;
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

        // match with backtracking
        public boolean matches(final String line, final List<Integer> remaining)
        {
            if (StringUtils.isEmpty(line)) // the string is now empty, but we still have something to match
            {
                return false;
            }
            // char match if the rule is a terminal rule
            if (c!=0)
            {
                if (line.charAt(0) == c)
                {
                    // when the char matches, look at the remaining string with the remaining rules
                    return  (listMatches(line.substring(1), remaining));
                }
                else // when the char does not match, we are done
                {
                    return false;
                }
            }
            // left rule
            Rule leftRule=rules.get(left.get(0));
            // we need to match the current rule (left and maybe right side), and the the rest of the rules from the list,
            // and then also the remaining rules we got from the parent
            // this allows to do proper backtracking
            if (leftRule.matches(line, ListUtils.union(left.subList(1, left.size()), remaining)))
                return true;
            // when we have a right rule, try to match it too
            if (null!=right)
            {
                Rule rightRule = rules.get(right.get(0));
                return rightRule.matches(line, ListUtils.union(right.subList(1, right.size()), remaining));
            }
            return false;
        }

        private boolean listMatches(String line, final List<Integer> rules)
        {
            // when we have no rules left, the line must also be empty
            if (0==rules.size())
            {
                return 0==line.length();
            }
            Rule rule = Day192.rules.get(rules.get(0));
            return rule.matches(line, rules.subList(1, rules.size()));
        }

        @Override
        public String toString()
        {
            return "Rule{" +
                    "_id=" + _id +
                    ", c=" + c +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}
