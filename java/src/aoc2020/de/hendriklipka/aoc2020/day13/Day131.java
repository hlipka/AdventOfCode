package de.hendriklipka.aoc2020.day13;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 13.12.20
 * Time: 22:40
 */
public class Day131
{
    private static final Comparator<? super Pair<Integer, Integer>> waitTimeComp = Comparator.comparingInt(Pair<Integer, Integer>::getRight);

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("../data/2020/day13.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            final int timestamp=Integer.parseInt(lines.get(0));
            final Function<? super Integer, Pair<Integer, Integer>> waitCalc = (Function<Integer, Pair<Integer, Integer>>) busID -> {
                int waitTime = busID-(timestamp % busID);
                return Pair.of(busID, waitTime);
            };
            Pair<Integer, Integer> bus = Arrays.stream(lines.get(1).split(",")).filter(b -> !b.equals("x")).map(Integer::parseInt).map(waitCalc).min(waitTimeComp).orElseThrow();
            System.out.println(bus.getLeft()+"/"+bus.getRight()+"/"+ bus.getLeft()*bus.getRight());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
