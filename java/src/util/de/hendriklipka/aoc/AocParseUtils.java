package de.hendriklipka.aoc;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 02.12.22
 * Time: 08:04
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AocParseUtils
{
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

    public static Map<String, String> parseIntoStringMap(String string, String elementDelimiter, String partDelimiter, String boundaries)
    {
        string = StringUtils.replaceChars(string, boundaries, "");

        Map<String, String> map = new HashMap<>();
        String[] partParts=string.split(elementDelimiter);
        for (String pp: partParts)
        {
            String[] values=pp.split(partDelimiter);
            map.put(values[0], values[1]);
        }

        return map;
    }

    public static Map<String, Integer> parseIntoIntMap(String string, String elementDelimiter, String partDelimiter, String boundaries)
    {
        string = StringUtils.replaceChars(string, boundaries, "");

        Map<String, Integer> map = new HashMap<>();
        String[] partParts=string.split(elementDelimiter);
        for (String pp: partParts)
        {
            String[] values=pp.split(partDelimiter);
            map.put(values[0], Integer.parseInt(values[1]));
        }

        return map;
    }

    public static List<Integer> splitLineToInts(String line)
    {
        return splitLineToInts(line, ',');
    }

    public static List<Integer> splitLineToInts(String line, char separator)
    {
        String[] nums = StringUtils.split(line, separator);
        final List<Integer> result=new ArrayList<>(nums.length);
        for (String s: nums)
            result.add(Integer.parseInt(s));
        return result;

    }

}
