package de.hendriklipka.aoc2020.day10;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day102
{
    public static long count=0;

    public static void main(String[] args)
    {
        try
        {
            List<Integer> jolts = FileUtils.readLines(new File("../data/2020/day10.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Integer::parseInt).sorted().collect(Collectors.toList());
            jolts.add(0, 0);
            // brute force the possible combinations
            // Note: for the real data set this is too slow, see 'Day102a' for how to calculate the result
            checkPossibleAdapters(jolts, 0);
            System.out.println(count);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void checkPossibleAdapters(List<Integer> jolts, int currentAdapterPos)
    {
        if (currentAdapterPos==jolts.size()-1) // the last adapter is always part of the chain, so we can stop there
        {
            count++;
            if (0==(count%10000000))
            {
                System.out.println(count);
            }
            return;
        }
        long currentJolt = jolts.get(currentAdapterPos);
        List<Integer> possibleNextJolts=getNextJolts(jolts, currentJolt, currentAdapterPos);
        for (int nextPos: possibleNextJolts)
        {
            checkPossibleAdapters(jolts, nextPos);
        }
    }

    // get the list of adapter that can be used for the next step - somewhere between 1 and 3 elements
    private static List<Integer> getNextJolts(List<Integer> jolts, long currentJolt, int currentAdapterPos)
    {
        List<Integer> result=new ArrayList<>(3);
        currentAdapterPos++; // we need to start with the next adapter from the list
        while (currentAdapterPos<jolts.size() && (jolts.get(currentAdapterPos) - currentJolt) <= 3)
        {
            result.add(currentAdapterPos);
            currentAdapterPos++;
        }
        return result;
    }
}
