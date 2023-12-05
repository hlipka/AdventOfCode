package de.hendriklipka.aoc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 02.12.22
 * Time: 08:04
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AocParseUtils
{
    public static List<List<Integer>> getIntegerBlocks(final String yearName, final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);

        List<List<Integer>> blocks = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        blocks.add(current);
        for (String line : lines)
        {
            if (StringUtils.isBlank(line))
            {
                current = new ArrayList<>();
                blocks.add(current);
                continue;
            }
            current.add(Integer.parseInt(line));
        }
        return blocks;
    }

    private static File getDataFileName(final String yearName, final String dayName)
    {
        return new File("../data/" + yearName +"/" + dayName + ".txt");
    }

    public static List<List<String>> getStringBlocks(final String yearName, final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);

        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        blocks.add(current);
        for (String line : lines)
        {
            if (StringUtils.isBlank(line))
            {
                current = new ArrayList<>();
                blocks.add(current);
                continue;
            }
            current.add(line);
        }
        if (blocks.get(blocks.size() - 1).isEmpty())
        {
            blocks.remove(blocks.size()-1);
        }
        return blocks;
    }


    public static List<List<String>> getLineWords(final String yearName, final String dayName, final String separator) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);
        return lines.stream().filter(StringUtils::isNotBlank).map(l->Arrays.asList(StringUtils.split(l, separator))).collect(Collectors.toList());
    }

    public static List<String> getFirstLineWords(final String yearName, final String dayName, final String separator) throws IOException
    {
        String line = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).findFirst().get();
        return Arrays.stream(StringUtils.split(line, separator)).map(String::trim).toList();
    }

    public static List<List<Integer>> getLineIntegers(final String yearName, final String dayName, final String separator) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);
        return lines.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(l -> Arrays.stream(StringUtils.split(l, separator)).map(Integer::parseInt).collect(
                            Collectors.toList()))
                    .collect(Collectors.toList());
    }


    public static List<String> getLines(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public static List<List<String>> getLinesAsChars(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l->l.chars().mapToObj(c->String.valueOf((char)c)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<List<Integer>> getLinesAsDigits(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l->l.chars().mapToObj(c->c-'0').collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<String> parsePartsFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '"+pattern+"' in ["+str+"]");
        }
        final int count = m.groupCount();
        List<String> result = new ArrayList<>(count);
        for (int i=0;i<count; i++)
        {
            result.add(m.group(i+1));
        }
        return result;
    }

    public static String parseStringFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '" + pattern + "' in [" + str + "]");
        }
        return m.group(1);
    }

    public static int parseIntFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '" + pattern + "' in [" + str + "]");
        }
        return Integer.parseInt(m.group(1));
    }

    public static long parseLongFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '" + pattern + "' in [" + str + "]");
        }
        return Long.parseLong(m.group(1));
    }
}
