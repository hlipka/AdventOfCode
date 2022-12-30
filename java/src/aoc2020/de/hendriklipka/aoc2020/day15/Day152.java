package de.hendriklipka.aoc2020.day15;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day152
{
    private static final int POS = 30000000;

    // cache the last positions to speed up search
    public static Map<Integer, Integer> lastPos=new HashMap<>();
    public static int lastSayNum=0;

    public static void main(String[] args)
    {
        try
        {
            String line = FileUtils.readLines(new File("data/day15.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).findFirst().orElseThrow();
            List<Integer> numbers = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            for (int i=0;i<numbers.size()-1;i++)
            {
                lastPos.put(numbers.get(i),i);
            }
            while (numbers.size() < POS)
            {
                sayNumber(numbers);
            }
            System.out.println("last:"+lastSayNum);
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
        int lastNumPos = lastPos.getOrDefault(lastNum, -1);
        int sayNum;

        if (-1==lastNumPos)
        {
            sayNum=0;
        }
        else
        {
            sayNum=lastIndex-lastNumPos;
        }

        lastPos.put(lastNum, lastIndex);
        lastSayNum=sayNum;
        numbers.add(sayNum);
    }
}
