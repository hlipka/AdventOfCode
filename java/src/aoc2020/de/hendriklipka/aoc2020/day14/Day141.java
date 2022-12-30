package de.hendriklipka.aoc2020.day14;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day141
{
    private static final Map<Integer, Long> memory = new HashMap<>();
    private static Mask currentMask;

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("../data/2020/day14.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()).forEach(Day141::process);
            System.out.println(memory);
            long sum= memory.values().stream().reduce(0L, Long::sum);
            System.out.println(sum);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void process(String line)
    {
        if (line.startsWith("mask"))
        {
            currentMask = new Mask(line);
        }
        else if (line.startsWith("mem"))
        {
            setMemory(line);
        }
    }

    private static final Pattern MEM_SET = Pattern.compile("mem\\[([0-9]+)] = ([0-9]+)");

    private static void setMemory(String line)
    {
        Matcher m = MEM_SET.matcher(line);
        if (!m.matches())
        {
            return;
        }
        String addrStr=m.group(1);
        String valueStr = m.group(2);
        int addr=Integer.parseInt(addrStr);
        long value = Long.parseLong(valueStr);
        long maskedValue=(value&currentMask.andMask)|currentMask.orMask;
        memory.put(addr, maskedValue);
    }

    private static class Mask
    {
        public long andMask=0;
        public long orMask=0;

        public Mask(String def)
        {
            // def: 'mask = X'
            String mask=def.substring(7);
            mask.chars().forEach(this::setMask);
            andMask=~andMask;
        }

        private void setMask(int i)
        {
            andMask = andMask<<1;
            orMask = orMask<<1;
            if (i=='1')
            {
                orMask+=1;
            }
            else if (i=='0')
            {
                andMask+=1;
            }
        }
    }
}
