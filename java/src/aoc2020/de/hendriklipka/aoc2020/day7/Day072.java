package de.hendriklipka.aoc2020.day7;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 07.12.20
 * Time: 23:11
 */
public class Day072
{
    static Map<String, List<String>> contains = new HashMap<>();

    static final Pattern START = Pattern.compile("(.*) bags contain");
    static final Pattern CONTAINS = Pattern.compile("(\\d+ .*?) bags?[,.]");
    static final Pattern COUNT = Pattern.compile("(\\d+) (.*)");

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("data/day7.txt"), StandardCharsets.UTF_8).forEach(Day072::parseRule);
            int count = countBags("shiny gold");
            System.out.println(count);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static int countBags(final String color)
    {
        int count=0;
        List<String> bags = contains.get(color);
        if (null==bags)
            return 0;
        for (String bag: bags)
        {
            Matcher mb=COUNT.matcher(bag);
            mb.matches();
            String numStr=mb.group(1);
            String colorStr=mb.group(2);
            int num=Integer.parseInt(numStr);
            count+=num;
            count+=num*countBags(colorStr);
        }
        return count;
    }

    private static void parseRule(final String rule)
    {
        Matcher mb= START.matcher(rule);
        if (mb.find())
        {
            String bagName = mb.group(1);
            List<String> contained=new ArrayList<>();
            Matcher mc = CONTAINS.matcher(rule);
            while (mc.find())
            {
                contained.add(mc.group(1));
            }
            contains.put(bagName, contained);
        }
    }
}
