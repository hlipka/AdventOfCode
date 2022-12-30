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
public class Day071
{
    static Map<String, List<String>> contains = new HashMap<>();

    static final Pattern START = Pattern.compile("(.*) bags contain");
    static final Pattern CONTAINS = Pattern.compile("\\d+ (.*?) bags?[,.]");

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("data/day7.txt"), StandardCharsets.UTF_8).forEach(Day071::parseRule);
//            System.out.println(contains);
            Map<String, List<String>> containedIn = mapRules();
//            System.out.println(containedIn);
            Set<String> visited = new HashSet<>();
            int count = countColors(containedIn, "shiny gold", visited);
            System.out.println(count-1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static int countColors(final Map<String, List<String>> containedIn, String colorName, final Set<String> visited)
    {
        int count=1;
        List<String> nextColors =containedIn.get(colorName);
        if (null==nextColors)
        {
//            System.out.println("no entry for "+colorName);
            return 1;
        }
//        count+=nextColors.size();
        for (String color: nextColors)
        {
            if (!visited.contains(color))
            {
                count+=countColors(containedIn, color, visited);
            }
            visited.add(color);
        }
        return count;
    }

    private static Map<String, List<String>> mapRules()
    {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, List<String>>e:contains.entrySet())
        {
            String bagName=e.getKey();
            for (String bag: e.getValue())
            {
                List<String> mapped = result.computeIfAbsent(bag, s -> new ArrayList<>());
                mapped.add(bagName);
            }
        }
        return result;
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
