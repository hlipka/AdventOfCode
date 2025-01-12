package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day07 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day07().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Pair<String, String>> required = new ArrayList<>();
        Set<String> remainingSteps = new HashSet<>();
        final var noNeeds = readDataAndFindFirstStep(required, remainingSteps);
        String firstStep = noNeeds.get(0);
        remainingSteps.remove(firstStep);

        StringBuffer order=new StringBuffer();
        order.append(firstStep);
        Set<String> alreadyDone = new HashSet<>();
        alreadyDone.add(firstStep);
        while (!remainingSteps.isEmpty())
        {
            final var next = findAvailableSteps(remainingSteps, required, alreadyDone);
            String nextPart = next.get(0);
            alreadyDone.add(nextPart);
            order.append(nextPart);
            remainingSteps.remove(nextPart);

        }
        return order;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final int workerCount=isExample?2:5;
        final int timeForA=isExample?1:61;
        
        List<Pair<String, String>> required = new ArrayList<>();
        Set<String> remainingSteps = new HashSet<>();
        final var firstSteps = readDataAndFindFirstStep(required, remainingSteps);

        // create all work which can start right away
        List<Pair<String, Integer>> worker=new ArrayList<>();
        for (String step : firstSteps)
            worker.add(Pair.of(step, timeForA+((int)step.charAt(0)-'A')));

        Set<String> alreadyDone = new HashSet<>();
        int time=0;
        while (!remainingSteps.isEmpty() || !worker.isEmpty())
        {
            time++;
            // find out which work has finished
            for (Pair<String, Integer> work: worker)
            {
                if (work.getRight()<=time)
                {
                    alreadyDone.add(work.getLeft());
                }
            }
            // remove all finished work (cannot be done in the loop above because of concurrent modification)
            CollectionUtils.filter(worker, w->!alreadyDone.contains(w.getLeft()));

            // do we have free workers?
            if (worker.size()==workerCount)
                continue;

            final var next = findAvailableSteps(remainingSteps, required, alreadyDone);
            while (worker.size()<workerCount && !next.isEmpty())
            {
                String nextPart = next.remove(0);
                remainingSteps.remove(nextPart);
                worker.add(Pair.of(nextPart, time + timeForA + ((int) nextPart.charAt(0) - 'A')));
            }
        }
        // 968 is too high
        return time;
    }

    private static List<String> findAvailableSteps(final Set<String> remainingSteps, final List<Pair<String, String>> required, final Set<String> alreadyDone)
    {
        List<String> next = new ArrayList<>(remainingSteps);
        // for each remaining step, verify that all requirements are fulfilled
        for (String step : remainingSteps)
        {
            for (Pair<String, String> e : required)
            {
                if (e.getRight().equals(step) && !alreadyDone.contains(e.getLeft()))
                {
                    next.remove(step);
                    break;
                }
            }
        }
        next.sort(String::compareTo);
        return next;
    }

    private List<String> readDataAndFindFirstStep(final List<Pair<String, String>> required, final Set<String> remainingSteps) throws IOException
    {
        data.getLines().forEach(l ->
        {
            List<String> instr = AocParseUtils.parsePartsFromString(l, "Step (\\w+) must be finished before step (\\w+) can begin.");
            required.add(Pair.of(instr.get(0), instr.get(1)));
            remainingSteps.add(instr.get(0));
            remainingSteps.add(instr.get(1));
        });

        // find the parts which does not need anything
        List<String> noNeeds = new ArrayList<>(
                CollectionUtils.subtract(required.stream().map(Pair::getLeft).collect(Collectors.toSet()), required.stream().map(Pair::getRight).collect(
                        Collectors.toSet())));
        noNeeds.sort(String::compareTo);
        return noNeeds;
    }
}
