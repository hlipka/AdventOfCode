package de.hendriklipka.aoc2023.day19;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * Brute-force thr solution
 *
 * better idea:
 * create 4 ranges for the x-m-a-s numbers, each [1-4000] (inclusive)
 * go recursively through the workflows, starting at 'in'
 * at each condition, branch with 'condition=true' and 'condition=false'
 * go to the workflow (or rule) which then comes next, with the new set of ranges
 * we either
 * - find an invalid combination (range mismatch) (then normally the next rule would kick in) -> stop here
 * - find 'A' -> multiply the length of all ranges together
 * - find 'R' -> stop here as well
 * we now just need to sum these
 */
public class Day19b
{
    public static final int MAX_VALUE = 4000;
    static Map<String, Workflow> rules;
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("2023", "day19");
            rules=parseRules(blocks.get(0));
            System.out.println(rules.size()+" rules");
            // optimize away rule which have no effective condition
            optimizeRules(rules);
            System.out.println("reduced to "+rules.size());
            // find all boundaries for each variable
            MultiValuedMap<Character, Integer > boundaries = findBoundaries();
            // this gives ranges with "value <|=|> boundary"
            // test all combinations
            // we test with boundary-1 and boundary to get everything (boundary+1 is handled with the next range)
            // from that we can calculate how big each resulting range is
            List<Integer> sValues=new ArrayList<>(boundaries.get('s'));
            sValues.sort(Integer::compareTo);
            List<Integer> xValues=new ArrayList<>(boundaries.get('x'));
            xValues.sort(Integer::compareTo);
            List<Integer> mValues=new ArrayList<>(boundaries.get('m'));
            mValues.sort(Integer::compareTo);
            List<Integer> aValues=new ArrayList<>(boundaries.get('a'));
            aValues.sort(Integer::compareTo);
            long count=countForS(sValues, xValues, mValues, aValues);
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long countForS(List<Integer> sValues, List<Integer> xValues, List<Integer> mValues, List<Integer> aValues)
    {
        /*
            assume we have distinct s-Rules such as (and maybe x>10):
            - s<10, x<10
            - s<13, x>19
            - s>13, x>10
            means we test s with
            - s=9, range length=9
            - s=10, range length=1
              (we don't need s=11, this is covered in the next range)
            - s=12, range length=2
            - s=13, range length=1
            - s=20, range length=7 (when the max value is 20 -> 20-13)
            and we test x with
            - x=9, range=9
            - x=10, range=1
            - x=18, range=8
            - x=19, range=1
            - x=20, range=1

            (for x, we test x=9, x=10, x=20 - only the latter succeeds and gives a range of 10)

            valid combinations should be
            s=1..9, x=1..9 (81)
            s=10..12, x=20 (3)
            s=14..20, x=11..20 (7*10)
        */

        int rangeStart=0;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        List<Future<Long>> tasks=new ArrayList<>();
        for (int boundary: sValues)
        {
            final int rangeLength=boundary-rangeStart-1;
            Future<Long> future = executorService.submit(() -> {
                long result=(long)rangeLength*countForX(boundary-1, xValues, mValues, aValues, rangeLength);
                result +=countForX(boundary, xValues, mValues, aValues, 1);
                System.out.println("done for "+boundary);
                return result;
            });
            tasks.add(future);
            rangeStart=boundary;
        }
        try
        {
            int rangeLength= MAX_VALUE - rangeStart;
            long count=(long)rangeLength*countForX(MAX_VALUE, xValues, mValues, aValues, rangeLength);
            for (Future<Long> task: tasks)
            {
                count+=task.get();
            }
            executorService.shutdownNow();
//            executorService.awaitTermination(2, TimeUnit.HOURS);
            return count;
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long countForX(int sValue, List<Integer> xValues, List<Integer> mValues, List<Integer> aValues, long factor)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: xValues)
        {
            int rangeLength=boundary-rangeStart-1;
            count+=(long)rangeLength*countForM(sValue, boundary-1, mValues, aValues, factor*rangeLength);
            count+=countForM(sValue, boundary, mValues, aValues, factor);
            rangeStart=boundary;
            System.out.println("done for s="+sValue+", x="+boundary);
        }
        int rangeLength= MAX_VALUE - rangeStart;
        count+=(long)rangeLength*countForM(sValue, MAX_VALUE, mValues, aValues, factor*rangeLength);

        return count;
    }

    private static long countForM(int sValue, int xValue, List<Integer> mValues, List<Integer> aValues, long factor)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: mValues)
        {
            int rangeLength=boundary-rangeStart-1;
            count+=(long)rangeLength*countForA(sValue, xValue, boundary-1, aValues, factor*rangeLength);
            count+=countForA(sValue, xValue, boundary, aValues, factor);
            rangeStart=boundary;
        }
        int rangeLength= MAX_VALUE - rangeStart;
        count+=(long)rangeLength*countForA(sValue, xValue, MAX_VALUE, aValues, factor*rangeLength);

        return count;
    }

    private static long countForA(int sValue, int xValue, int mValue, List<Integer> aValues, long factor)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: aValues)
        {
            int rangeLength=boundary-rangeStart-1;
            if (isValid(sValue, xValue, mValue, boundary-1, factor*rangeLength))
                count+= rangeLength;
            if (isValid(sValue, xValue, mValue, boundary, factor))
                count++;
            rangeStart=boundary;
        }
        int rangeLength= MAX_VALUE - rangeStart;
        if (isValid(sValue, xValue, mValue, MAX_VALUE, factor*rangeLength))
            count+= rangeLength;

        return count;
    }

    public static boolean isValid(int sValue, int xValue, int mValue, int aValue, long factor)
    {
        Part part=new Part(sValue, xValue, mValue, aValue);

        String wf="in";
        while (true)
        {
            Workflow w=rules.get(wf);
            if (null==w)
            {
                System.out.println();
            }
            wf= w.getNextWorkflow(part);
            if (wf.equals("A"))
            {
//                System.out.println("accept s="+sValue+", x="+xValue+", m="+mValue+", a="+aValue+" worth "+factor);
                return true;
            }
            if (wf.equals("R"))
                return false;
        }

    }

    private static MultiValuedMap<Character, Integer> findBoundaries()
    {
        MultiValuedMap<Character, Integer > boundaries=new HashSetValuedHashMap<>();
        for (Workflow wf: rules.values())
        {
            for (Rule r: wf.rules)
            {
                if (null!=r.condition)
                {
                    boundaries.put(r.condition.getName(), r.condition.getRangeValue());
                }
            }
        }
        return boundaries;
    }

    private static void optimizeRules(Map<String, Workflow> rules)
    {
        // find WFs where the target WF is the same for all rules
        // replace these rules by their common target
        boolean somethingReplaced;
        do
        {
            somethingReplaced = false;
            for (Workflow wf: rules.values())
            {
                String common=wf.getCommonTarget();
                if (null!=common)
                {
                    somethingReplaced=true;
                    rules.remove(wf.name); // remove the rule
                    replaceTarget(wf.name, common); // replace the obsolete WF with its target
                    break;
                }
            }
        } while (somethingReplaced);
    }

    private static void replaceTarget(String oldTarget, String newTarget)
    {
        for (Workflow wf: rules.values())
        {
            wf.replaceTarget(oldTarget, newTarget);
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

    private static RuleCondition parseCondition(String rule)
    {
        if (rule.contains("<"))
        {
            String[] ruleParts=rule.split("<");
            return new LessThan(ruleParts[0].charAt(0), Integer.parseInt(ruleParts[1]));
        }
        else if (rule.contains(">"))
        {
            String[] ruleParts=rule.split(">");
            return new GreaterThan(ruleParts[0].charAt(0), Integer.parseInt(ruleParts[1]));
        }
        throw new IllegalArgumentException("rule");
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

        public String getCommonTarget()
        {
            String common=rules.get(0).target;
            for (Rule r: rules)
            {
                if (!common.equals(r.target))
                    return null;
            }
            return common;
        }

        public void replaceTarget(String oldTarget, String newTarget)
        {
            for (Rule r: rules)
            {
                if (r.target.equals(oldTarget))
                    r.target=newTarget;
            }
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

        @Override
        public String toString()
        {
            return "Workflow{" +
                   "name='" + name + '\'' +
                   ", rules=" + rules +
                   '}';
        }
    }

    private static class Rule
    {
        public String target;
        RuleCondition condition;

        @Override
        public String toString()
        {
            if (null==condition)
                return "*:"+target;
            return condition+"=>"+target;
        }
    }

    private static class LessThan extends RuleCondition implements Predicate<Part>
    {
        public LessThan(Character name, int value)
        {
            super(name, value);
        }

        @Override
        public boolean test(Part part)
        {
            return part.get(name)<value;
        }

        @Override
        public String toString()
        {
            return name+"<"+value;
        }
    }

    private static class GreaterThan extends RuleCondition
    {

        public GreaterThan(Character name, int value)
        {
            super(name, value);
        }

        @Override
        public boolean test(Part part)
        {
            return part.get(name)>value;
        }

        @Override
        public String toString()
        {
            return name+">"+value;
        }
    }

    private static class Part
    {
        Map<Character, Integer> values = new HashMap<>();
        public Part(int sValue, int xValue, int mValue, int aValue)
        {
            values.put('s', sValue);
            values.put('x', xValue);
            values.put('m', mValue);
            values.put('a', aValue);
        }

        public int get(Character name)
        {
            return values.get(name);
        }
    }

    public abstract static class RuleCondition implements Predicate<Part>
    {
        protected final Character name;
        protected final int value;

        public RuleCondition(Character name, int value)
        {
            this.name = name;
            this.value = value;
        }

        public Character getName()
        {
            return name;
        }

        public int getRangeValue()
        {
            return value;
        }
    }
}

