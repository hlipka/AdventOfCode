package de.hendriklipka.aoc2023.day19;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * Brute-force the solution (more-or-less)
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
            List<List<String>> blocks = AocDataFileUtils.getStringBlocks("2023", "day19");
            rules=parseRules(blocks.get(0));
            System.out.println(rules.size() + " rules, " + rules.values().stream().mapToInt(wf->wf.conditions.size()).sum() + " conditions");
            // optimize away rules which have no effective condition
            System.out.println("optimizing");
            optimizeRules(rules);
            System.out.println(rules.size() + " rules, " + rules.values().stream().mapToInt(wf -> wf.conditions.size()).sum() + " conditions");
//            System.out.println(StringUtils.join(rules.values(), "\n"));
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
            StopWatch watch = new StopWatch();
            watch.start();
            long count=countForS(sValues, xValues, mValues, aValues);
            System.out.println(count);
            System.out.println(watch.getTime()/1000+"s");
            // AMD Ryzen 5 2600X - 12 threads, takes 491s
            // AMD Ryzen 9 7900 - 24 threads, take 149s
            // Intel i9-10980XE - 36 threads, take 237s
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

            valid combinations are then (in multiple ranges, though)
            s=1..9, x=1..9 (81)
            s=10..12, x=20 (3)
            s=14..20, x=11..20 (7*10)
        */

        int rangeStart=0;
        ExecutorService executorService = Executors.newWorkStealingPool();
        List<Future<Long>> tasks=new ArrayList<>();
        for (int boundary: sValues)
        {
            final int rangeLength=boundary-rangeStart-1;
            Future<Long> future = executorService.submit(() -> {
                long result=(long)rangeLength*countForX(boundary-1, xValues, mValues, aValues);
                result +=countForX(boundary, xValues, mValues, aValues);
                System.out.println("done for "+boundary);
                return result;
            });
            tasks.add(future);
            rangeStart=boundary;
        }
        try
        {
            int rangeLength= MAX_VALUE - rangeStart;
            long count=(long)rangeLength*countForX(MAX_VALUE, xValues, mValues, aValues);
            for (Future<Long> task: tasks)
            {
                count+=task.get();
            }
            executorService.shutdownNow();
            return count;
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long countForX(int sValue, List<Integer> xValues, List<Integer> mValues, List<Integer> aValues)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: xValues)
        {
            int rangeLength=boundary-rangeStart-1;
            count+=(long)rangeLength*countForM(sValue, boundary-1, mValues, aValues);
            count+=countForM(sValue, boundary, mValues, aValues);
            rangeStart=boundary;
        }
        int rangeLength= MAX_VALUE - rangeStart;
        count+=(long)rangeLength*countForM(sValue, MAX_VALUE, mValues, aValues);

        return count;
    }

    private static long countForM(int sValue, int xValue, List<Integer> mValues, List<Integer> aValues)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: mValues)
        {
            int rangeLength=boundary-rangeStart-1;
            count+=(long)rangeLength*countForA(sValue, xValue, boundary-1, aValues);
            count+=countForA(sValue, xValue, boundary, aValues);
            rangeStart=boundary;
        }
        int rangeLength= MAX_VALUE - rangeStart;
        count+=(long)rangeLength*countForA(sValue, xValue, MAX_VALUE, aValues);

        return count;
    }

    private static long countForA(int sValue, int xValue, int mValue, List<Integer> aValues)
    {
        long count=0;
        int rangeStart=0;
        for (int boundary: aValues)
        {
            int rangeLength=boundary-rangeStart-1;
            if (isValid(sValue, xValue, mValue, boundary-1))
                count+= rangeLength;
            if (isValid(sValue, xValue, mValue, boundary))
                count++;
            rangeStart=boundary;
        }
        int rangeLength= MAX_VALUE - rangeStart;
        if (isValid(sValue, xValue, mValue, MAX_VALUE))
            count+= rangeLength;

        return count;
    }

    public static boolean isValid(int sValue, int xValue, int mValue, int aValue)
    {
        Part part=new Part(sValue, xValue, mValue, aValue);

        String wf="in";
        while (true)
        {
            wf= rules.get(wf).getNextWorkflow(part);
            if (wf.equals("A"))
            {
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
            for (Rule r: wf.conditions)
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
        boolean somethingReplaced;
        do
        {
            somethingReplaced = false;
            for (Workflow wf: rules.values())
            {
                while (true)
                {
                    // when the last two conditions in the workflow have the same target, the second-to-last is useless, and can be removed (they both end up at
                    // the same target anyway)
                    List<Rule> wfr = wf.conditions;
                    if (wfr.size()>1 && wfr.get(wfr.size() - 2).target.equals(wfr.get(wfr.size() - 1).target))
                    {
                        wfr.remove(wfr.size() - 2);
                    }
                    else
                    {
                        break;
                    }
                }
                // when we now have only one condition left, it is a no-op rule, and we can optimize the workflow away
                if (wf.conditions.size() == 1)
                {
                    somethingReplaced = true;
                    rules.remove(wf.name); // remove the rule
                    replaceTarget(wf.name, wf.conditions.get(0).target); // replace the obsolete WF with its only target
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
            if (ruleParts[0].contains("<"))
            {
                String[] conditionParts= ruleParts[0].split("<");
                r.condition = new LessThan(conditionParts[0].charAt(0), Integer.parseInt(conditionParts[1]));
            }
            else if (ruleParts[0].contains(">"))
            {
                String[] conditionParts= ruleParts[0].split(">");
                r.condition = new GreaterThan(conditionParts[0].charAt(0), Integer.parseInt(conditionParts[1]));
            }
            else
            {
                throw new IllegalArgumentException(ruleParts[0]);
            }
        }
        else {
            r.target=ruleStr;
        }
        return r;
    }

    private static class Workflow
    {
        final String name;
        final List<Rule> conditions =new ArrayList<>();

        public Workflow(String name)
        {
            this.name=name;
        }

        public void addRule(Rule rule)
        {
            conditions.add(rule);
        }

        public String getCommonTarget()
        {
            String common= conditions.get(0).target;
            for (Rule r: conditions)
            {
                if (!common.equals(r.target))
                    return null;
            }
            return common;
        }

        public void replaceTarget(String oldTarget, String newTarget)
        {
            for (Rule r: conditions)
            {
                if (r.target.equals(oldTarget))
                    r.target=newTarget;
            }
        }

        public String getNextWorkflow(Part part)
        {
            for (Rule rule: conditions)
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
                   ", rules=" + conditions +
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
        int x,m,a,s;
        public Part(int sValue, int xValue, int mValue, int aValue)
        {
            s=sValue;
            x=xValue;
            m=mValue;
            a=aValue;
        }

        public int get(Character name)
        {
            return switch(name)
            {
                case 'x'->x;
                case 'm'->m;
                case 'a'->a;
                case 's'->s;
                default -> throw new IllegalStateException("Unexpected value: " + name);
            }
            ;
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

