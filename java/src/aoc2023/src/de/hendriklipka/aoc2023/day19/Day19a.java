package de.hendriklipka.aoc2023.day19;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Day19a
{
    static Map<String, Workflow> rules;
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("2023", "day19");
            rules=parseRules(blocks.get(0));
            List<Part> parts=blocks.get(1).stream().map(Day19a::parsePart).toList();
            int result=parts.stream().filter(Day19a::isValid).mapToInt(Part::value).sum();
            System.out.println(result);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValid(Part part)
    {
        String wf="in";
        while (true)
        {
            Workflow w=rules.get(wf);
            wf=w.getNextWorkflow(part);
            if (wf.equals("A"))
                return true;
            if (wf.equals("R"))
                return false;
        }
    }

    private static Map<String, Workflow> parseRules(List<String> strings)
    {
        Map<String, Workflow> ruleList=new HashMap<>();
        for (String s: strings)
        {
            Workflow rl=parseRuleLine(s);
            ruleList.put(rl.name, rl);
        }
        return ruleList;
    }

    private static Workflow parseRuleLine(String line)
    {
        String name=AocParseUtils.parseStringFromString(line, "(\\w+)\\{.*");
        Workflow rl=new Workflow(name);
        String[] ruleList= AocParseUtils.parseStringFromString(line, "\\w+\\{(.*)\\}").split(",");

        for (String ruleStr: ruleList)
        {
            rl.addRule(parseRule(ruleStr));
        }
        return rl;
    }

    private static Rule parseRule(String ruleStr)
    {
        Rule r=new Rule();
        if (ruleStr.contains(":"))
        {
            String[] ruleParts=ruleStr.split(":");
            r.target=ruleParts[1];
            r.condition=parseCondition(ruleParts[0]);
        }
        else {
            r.target=ruleStr;
        }
        return r;
    }

    private static Predicate<Part> parseCondition(String rule)
    {
        if (rule.contains("<"))
        {
            String[] ruleParts=rule.split("<");
            return new LessThan(ruleParts[0], Integer.parseInt(ruleParts[1]));
        }
        else if (rule.contains(">"))
        {
            String[] ruleParts=rule.split(">");
            return new GreaterThan(ruleParts[0], Integer.parseInt(ruleParts[1]));
        }
        throw new IllegalArgumentException("rule");
    }

    private static Part parsePart(String line)
    {
        Part part=new Part();
        String partDesc= AocParseUtils.parseStringFromString(line, "\\{(.+)\\}");
        String[] partParts=partDesc.split(",");
        for (String pp: partParts)
        {
            String[] values=pp.split("=");
            part.set(values[0], Integer.parseInt(values[1]));
        }
        return part;
    }

    private static class Workflow
    {
        final String name;
        final List<Rule> rules=new ArrayList<>();

        public Workflow(String name)
        {
            this.name=name;
        }

        public void addRule(Rule rule)
        {
            rules.add(rule);
        }

        public String getNextWorkflow(Part part)
        {
            for (Rule rule: rules)
            {
                if (rule.condition==null)
                {
                    return rule.target;
                }
                if (rule.condition.test(part))
                {
                    return rule.target;
                }
            }
            throw new IllegalArgumentException("part "+part+" does not match anything!");
        }
    }

    private static class Rule
    {
        public String target;
        public Predicate<Part> condition;
    }

    private static class Part
    {

        private Map<String, Integer> values = new HashMap<>();

        public int get(String name)
        {
            return values.get(name);
        }

        public void set(String name, int value)
        {
            values.put(name, value);
        }

        public int value()
        {
            int sum=0;
            for (Integer i: values.values())
                sum+=i;
            return sum;
        }
    }

    private static class LessThan implements Predicate<Part>
    {
        private final String name;
        private final int value;

        public LessThan(String name, int value)
        {
            this.name=name;
            this.value=value;
        }

        @Override
        public boolean test(Part part)
        {
            return part.get(name)<value;
        }
    }

    private static class GreaterThan implements Predicate<Part>
    {
        private final String name;
        private final int value;

        public GreaterThan(String name, int value)
        {
            this.name=name;
            this.value=value;
        }

        @Override
        public boolean test(Part part)
        {
            return part.get(name)>value;
        }
    }
}

