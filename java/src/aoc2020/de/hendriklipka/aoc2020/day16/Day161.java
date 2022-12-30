package de.hendriklipka.aoc2020.day16;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day161
{
    private static final Pattern RULE = Pattern.compile("(.+): (\\d+)-(\\d+) or (\\d+)-(\\d+)");

    private static List<Integer> ticket;
    private static List<List<Integer>> tickets = new ArrayList<>();
    private static List<Rule> rules = new ArrayList<>();

    private static long errorSum;

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day16.txt"), StandardCharsets.UTF_8).stream().collect(Collectors.toList());
            Iterator<String> li = lines.iterator();
            String line=li.next();
            while (StringUtils.isNotBlank(line))
            {
                rules.add(parseRule(line));
                line=li.next();
            }
            li.next();
            String ticketStr=li.next();
            ticket=parseTicket(ticketStr);

            li.next();
            li.next();
            line=li.next();
            while (StringUtils.isNotBlank(line))
            {
                tickets.add(parseTicket(line));
                if (!li.hasNext())
                {
                    break;
                }
                line=li.next();
            }

            for (List<Integer> ti: tickets)
            {
                validateTicket(ti);
            }
            System.out.println(errorSum);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void validateTicket(List<Integer> ti)
    {
        for (int num: ti)
        {
            if (!validateField(num))
            {
                errorSum+=(long)num;
            }
        }
    }

    private static boolean validateField(int num)
    {
        for (Rule rule: rules)
        {
            if (rule.fitsRule(num))
            {
                return true;
            }
        }
        return false;
    }

    private static Rule parseRule(String line)
    {
        Matcher m = RULE.matcher(line);
        if (!m.matches())
        {
            throw new IllegalArgumentException("rule ["+line+"] is not valid");
        }
        return new Rule(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5));
    }

    private static List<Integer> parseTicket(String line)
    {
        return Arrays.stream(StringUtils.split(line, ",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private static class Rule
    {

        private final String _name;
        private final Range _range1;
        private final Range _range2;

        public Rule(String name, String from1, String to1, String from2, String to2)
        {
            _name=name;
            _range1 = new Range(from1, to1);
            _range2 = new Range(from2, to2);
        }

        public boolean fitsRule(int i)
        {
            return (_range1.isInRange(i) || _range2.isInRange(i));
        }
    }

    private static class Range
    {

        private final int _from;
        private final int _to;

        public Range(String from, String to)
        {
            _from=Integer.parseInt(from);
            _to=Integer.parseInt(to);
        }

        public boolean isInRange(int i)
        {
            return i>=_from && i<=_to;
        }
    }
}
