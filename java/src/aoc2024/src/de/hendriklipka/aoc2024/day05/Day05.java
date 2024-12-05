package de.hendriklipka.aoc2024.day05;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day05 extends AocPuzzle
{
    public Day05()
    {
        super("2024", "05");
    }

    public static void main(String[] args)
    {
        new Day05().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<Rule> rules = blocks.get(0).stream().map(Rule::new).toList();
        List<List<Integer>> updates = blocks.get(1).stream().map(AocParseUtils::splitLineToInts).toList();

        return updates.stream().filter(u-> rules.stream().allMatch(r->r.isUpdateOK(u))).mapToInt(u-> u.get(u.size() / 2)).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<Rule> rules = blocks.get(0).stream().map(Rule::new).toList();
        List<List<Integer>> updates = blocks.get(1).stream().map(AocParseUtils::splitLineToInts).toList();

        return updates.stream().filter(u-> !rules.stream().allMatch(r -> r.isUpdateOK(u))).map(u->fixUpdate(u, rules)).mapToInt(u1 -> u1.get(u1.size() / 2)).sum();
    }

    private List<Integer> fixUpdate(final List<Integer> u, final List<Rule> rules)
    {
        final List<Integer> result = new ArrayList<>(u);
        // checkAndFixUpdate() swaps the two wrong numbers with each other 8when they are not correct already)
        // so this is basically a bubble sort, but we compare remote pairs instead of direct neighbours
        // as long as there are no conflicting rules this will converge eventually
        while (true)
        {
            boolean allSorted=true;
            for (Rule r: rules)
            {
                allSorted &= r.checkAndFixUpdate(result);
            }
            if (allSorted)
                break;
        }
        return result;
    }

    private static class Rule
    {

        private final int _first;
        private final int _second;

        public Rule(final String l)
        {
            _first=AocParseUtils.parseIntFromString(l, "(\\d+)\\|.*");
            _second=AocParseUtils.parseIntFromString(l, ".*\\|(\\d+)");
        }

        public boolean isUpdateOK(final List<Integer> update)
        {
            int firstPos=update.indexOf(_first);
            int secondPos=update.indexOf(_second);
            if (firstPos==-1||secondPos==-1)
                return true;
            return firstPos<secondPos;
        }

        public boolean checkAndFixUpdate(final List<Integer> update)
        {
            int firstPos=update.indexOf(_first);
            int secondPos=update.indexOf(_second);
            if (firstPos==-1||secondPos==-1)
                return true;
            if (firstPos<secondPos)
                return true;
            update.set(secondPos, _first);
            update.set(firstPos, _second);
            return false;
        }
    }
}
