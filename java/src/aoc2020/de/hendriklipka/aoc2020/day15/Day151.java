package de.hendriklipka.aoc2020.day15;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day151
{
    public static void main(String[] args)
    {
        try
        {
            String line = FileUtils.readLines(new File("data/day15.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).findFirst().orElseThrow();
            List<Integer> numbers = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            while (numbers.size() <2020)
            {
                sayNumber(numbers);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void sayNumber(List<Integer> numbers)
    {
        int lastIndex = numbers.size() - 1;
        int lastNum = numbers.get(lastIndex);
        int lastNumPos = numbers.subList(0, lastIndex).lastIndexOf(lastNum);
        if (-1==lastNumPos)
        {
            System.out.println("0");
            numbers.add(0);
        }
        else
        {
            int diff=lastIndex-lastNumPos;
            System.out.println(diff);
            numbers.add(diff);
        }
    }
}
