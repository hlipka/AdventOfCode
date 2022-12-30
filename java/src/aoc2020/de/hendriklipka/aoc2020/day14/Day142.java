package de.hendriklipka.aoc2020.day14;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day142
{
    private static final Map<Long, Long> memory = new HashMap<>();
    private static Mask currentMask;

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("../data/2020/day14.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()).forEach(Day142::process);
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
        long addr=Integer.parseInt(addrStr);
        addr|= currentMask.orMask;
        long value = Long.parseLong(valueStr);
        putValues(addr, value, currentMask.floatingBits);
    }

    private static void putValues(final long addr, final long value, final List<Long> bits)
    {
        if (0==bits.size())
        {
            memory.put(addr, value);
        }
        else
        {
            long bitValue=bits.get(0);
            final List<Long> newBits = bits.subList(1, bits.size());
            boolean addrBitSet=(addr&bitValue)>0;
            putValues(addr, value, newBits);
            putValues(addrBitSet?addr-bitValue:addr+bitValue, value, newBits);
        }
    }

    private static class Mask
    {
        public long orMask=0;
        public long floatingPos =1L<<35;
        List<Long> floatingBits = new ArrayList<>(36);

        public Mask(String def)
        {
            // def: 'mask = X'
            String mask=def.substring(7);
            mask.chars().forEach(this::setMask);
        }

        private void setMask(int i)
        {
            orMask = orMask<<1;
            if (i=='1')
            {
                orMask+=1;
            }
            else if (i=='X')
            {
                floatingBits.add(floatingPos);
            }
            floatingPos = floatingPos >>>1; // unsigned shift
        }
    }
}
