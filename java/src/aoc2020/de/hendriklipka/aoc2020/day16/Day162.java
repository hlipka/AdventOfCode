package de.hendriklipka.aoc2020.day16;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day162
{
    private static final Pattern RULE = Pattern.compile("(.+): (\\d+)-(\\d+) or (\\d+)-(\\d+)");

    private static List<Integer> myTicket;
    private static List<List<Integer>> tickets = new ArrayList<>();
    private static List<Rule> rules = new ArrayList<>();

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = new ArrayList<>(FileUtils.readLines(new File("../data/2020/day16.txt"), StandardCharsets.UTF_8));
            Iterator<String> li = lines.iterator();
            String line=li.next();
            while (StringUtils.isNotBlank(line))
            {
                rules.add(parseRule(line));
                line=li.next();
            }
            li.next();
            String ticketStr=li.next();
            myTicket =parseTicket(ticketStr);

            li.next();
            li.next();
            line=li.next();
            while (StringUtils.isNotBlank(line))
            {
                List<Integer> ti = parseTicket(line);
                if (validateTicket(ti))
                {
                    tickets.add(ti);
                }
                if (!li.hasNext())
                {
                    break;
                }
                line=li.next();
            }
            tickets.add(myTicket);

            // rules might fit multiple columns, so we first track all candidates
            Map<String, List<Integer>> candidateColumns=new HashMap<>();

            int colCount=tickets.get(0).size();
            for (int column=0;column<colCount;column++)
            {
                final int currentColumn = column;
                List<Integer> columnData = tickets.stream().map(ti->ti.get(currentColumn)).sorted().collect(Collectors.toList());
                for (Rule rule: rules)
                {
                    if (ruleMatchesColumnData(rule, columnData))
                    {
                        List<Integer> cols = candidateColumns.computeIfAbsent(rule.getName(), s -> new ArrayList<>());
                        cols.add(column);
                    }
                }
            }

            boolean didAssign=true;
            while (didAssign)
            {
                didAssign=false;
                for (Rule rule: rules)
                {
                    List<Integer> cols=candidateColumns.get(rule.getName());
                    if (1==cols.size())
                    {
                        didAssign=true;
                        Integer column = cols.get(0);
                        System.out.println("assign "+rule.getName()+" to "+column);
                        rule.setColumn(column);
                        for (List<Integer> changeColumns: candidateColumns.values())
                        {
                            changeColumns.remove(column);
                        }
                    }
                }
            }

            long result=1;
            for (Rule rule: rules)
            {
                if (rule.getName().startsWith("departure"))
                {
                    result*= myTicket.get(rule.getColumn());
                }
            }
            System.out.println(result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean ruleMatchesColumnData(Rule rule, List<Integer> columnData)
    {
        for (Integer num: columnData)
        {
            if (!rule.fitsRule(num))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean validateTicket(List<Integer> ti)
    {
        for (int num: ti)
        {
            if (!validateField(num))
            {
                return false;
            }
        }
        return true;
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
        private int _column=-1;

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

        public int getColumn()
        {
            return _column;
        }

        public void setColumn(int _column)
        {
            this._column = _column;
        }

        public String getName()
        {
            return _name;
        }

        @Override
        public String toString()
        {
            return "Rule{" +
                    "_name='" + _name + '\'' +
                    ", _range1=" + _range1 +
                    ", _range2=" + _range2 +
                    ", _column=" + _column +
                    '}';
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

        @Override
        public String toString()
        {
            return "Range{" +
                    "_from=" + _from +
                    ", _to=" + _to +
                    '}';
        }
    }
}
