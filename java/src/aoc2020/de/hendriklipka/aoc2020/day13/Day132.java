package de.hendriklipka.aoc2020.day13;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 So assume the demo data:
 * period 7, offset 0
 * period 13, offset 1
 * period 59, offset 4
 * period 31, offset 6
 * period 19, offset 7
 We can treat all busses as starting at timestamp (busNo-offset), and then the all must depart at the same time. So the start times are
 * period 7, start 0
 * period 13, start -12
 * period 59, start -55
 * period 31, start -25
 * period 19, start -12
 The start times are also the remainders (when doing modulo operations)
 We can start with period 7 (so 0, 7, 14...), and find the first timestamp where it matches with one of the other busses. This will be 14, and the other bus is 13.
 From now on, the period will be 7*13=91, so the timestamps will be 14, 105, 196 and so on. Repeat for the other numbers.
 Observation: all the numbers are primes, so we can calculate the new period always by multiplication.
 */
public class Day132
{
    static long currentPeriod;
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day13.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            List<String> busses = Arrays.stream(lines.get(1).split(",")).collect(Collectors.toList());
            // determine offsets for the busses
            List<Pair<Integer, Integer>> busTimes=getBusTimes(busses);
            System.out.println(busTimes);
            long time=0;
            currentPeriod=busTimes.get(0).getLeft();
            busTimes.remove(0);
            // repeat until we have at least one bus to check
            while(busTimes.size()!=0)
            {
                // look at the busses from the right so we can remove without affecting the iteration
                for (int i=busTimes.size()-1;i>=0;i--)
                {
                    Pair<Integer, Integer> busTime = busTimes.get(i);
                    if (busTimeMatches(time, busTime))
                    {
                        currentPeriod*=busTime.getLeft();
                        System.out.println("bus "+busTime.getLeft()+" matches at "+time+", period is now "+currentPeriod);
                        busTimes.remove(i);
                    }
                }
                if (0!=busTimes.size())
                    time+=currentPeriod;
                if (time<0) // just to be sure
                {
                    System.err.println("overflow");
                    break;
                }
            }
            System.out.println(time);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static boolean busTimeMatches(long time, Pair<Integer, Integer> busTime)
    {
        return (time%busTime.getLeft())==(busTime.getLeft()-busTime.getRight());
    }

    private static List<Pair<Integer, Integer>> getBusTimes(List<String> busses)
    {
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i<busses.size(); i++)
        {
            String busIDStr = busses.get(i);
            if (!"x".equals(busIDStr))
            {
                int busID = Integer.parseInt(busIDStr);
                result.add(ImmutablePair.of(busID,i%busID));
            }
        }
        return result;
    }
}
