package de.hendriklipka.aoc;

import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AocDataFileUtils
{
    private String yearName;
    private String dayName;

    public AocDataFileUtils(final String yearName, final String dayName)
    {
        this.yearName = yearName;
        this.dayName = dayName;
    }

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

    public static File getDataFileName(final String yearName, final String dayName)
    {
        return new File("../data/" + yearName + "/" + dayName + ".txt");
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
            blocks.remove(blocks.size() - 1);
        }
        return blocks;
    }

    public static List<List<String>> getLineWords(final String yearName, final String dayName, final String separator) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);
        return lines.stream().filter(StringUtils::isNotBlank).map(l -> Arrays.asList(StringUtils.split(l, separator))).collect(Collectors.toList());
    }

    public static List<String> getFirstLineWords(final String yearName, final String dayName, final String separator) throws IOException
    {
        String line = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(
                StringUtils::isNotBlank).findFirst().get();
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

    public static List<List<Long>> getLineLongs(final String yearName, final String dayName, final String separator) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);
        return lines.stream()
                .filter(StringUtils::isNotBlank)
                .map(l -> Arrays.stream(StringUtils.split(l, separator)).map(Long::parseLong).collect(
                        Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<String> getLines(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(
                Collectors.toList());
    }

    public static List<List<String>> getLinesAsCharStrings(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l -> l.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static CharMatrix getLinesAsCharMatrix(final String yearName, final String dayName, char defaultChar) throws IOException
    {
        return new CharMatrix(getLinesAsChars(yearName, dayName), defaultChar);
    }

    public static IntMatrix getLinesAsIntMatrix(final String yearName, final String dayName, int defaultValue) throws IOException
    {
        return new IntMatrix(getLinesAsDigits(yearName, dayName), defaultValue);
    }

    public static List<List<Character>> getLinesAsChars(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l -> l.chars().mapToObj(c -> (char) c).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<List<Integer>> getLinesAsDigits(final String yearName, final String dayName) throws IOException
    {
        return FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l -> l.chars().mapToObj(c -> c - '0').collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<Integer> getLineAsInteger(final String yearName, final String dayName, final String separator) throws IOException
    {
        List<String> lines = FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8);
        return Arrays.stream(StringUtils.split(lines.get(0), separator)).map(Integer::parseInt).collect(
                Collectors.toList());
    }

    public static List<Integer> getLinesAsInt(final String yearName, final String dayName) throws IOException
    {
        return new ArrayList<>(FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(
                Integer::parseInt).toList());
    }

    public static List<Long> getLinesAsLong(final String yearName, final String dayName) throws IOException
    {
        return new ArrayList<>(FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(
                Long::parseLong).toList());
    }

    public static Map<String, List<String>> getLinesIntoStringMap(final String yearName, final String dayName, char partSeparator, char listSeparator) throws
            IOException
    {
        final Map<String, List<String>> result = new HashMap<>();

        FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(
                l->{
                    String[] parts = StringUtils.split(l, partSeparator);
                    if (result.containsKey(parts[0]))
                    {
                        throw new IllegalArgumentException("Duplicate key: " + parts[0]);
                    }
                    String[] lineParts = StringUtils.split(parts[1].trim(), listSeparator);
                    result.put(parts[0].trim(), Arrays.asList(lineParts));
                }
        );

        return result;
    }

    public static Map<String, List<Integer>> getLinesIntoStringIntMap(final String yearName, final String dayName, char partSeparator, char listSeparator) throws
            IOException
    {
        final Map<String, List<Integer>> result = new HashMap<>();
        FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(
                l->{
                    String[] parts = StringUtils.split(l, partSeparator);
                    if (result.containsKey(parts[0]))
                    {
                        throw new IllegalArgumentException("Duplicate key: " + parts[0]);
                    }
                    result.put(
                            parts[0].trim(),
                            AocParseUtils.splitLineToInts(parts[1].trim(), listSeparator)
                    );
                }
        );
        return result;
    }

    public static Map<String, List<Long>> getLinesIntoStringLongMap(final String yearName, final String dayName, char partSeparator, char listSeparator) throws
            IOException
    {
        final Map<String, List<Long>> result = new HashMap<>();
        FileUtils.readLines(getDataFileName(yearName, dayName), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(
                l->{
                    String[] parts = StringUtils.split(l, partSeparator);
                    if (result.containsKey(parts[0]))
                    {
                        throw new IllegalArgumentException("Duplicate key: " + parts[0]);
                    }
                    result.put(
                            parts[0].trim(),
                            AocParseUtils.splitLineToLongs(parts[1].trim(), listSeparator)
                    );
                }
        );
        return result;
    }

    ////////////

    public List<List<Integer>> getIntegerBlocks() throws IOException
    {
        return getIntegerBlocks(yearName, dayName);
    }

    public List<List<String>> getStringBlocks() throws IOException
    {
        return getStringBlocks(yearName, dayName);
    }

    public List<List<String>> getLineWords(final String separator) throws IOException
    {
        return getLineWords(yearName, dayName, separator);
    }

    public List<String> getFirstLineWords(final String separator) throws IOException
    {
        return getFirstLineWords(yearName, dayName, separator);
    }

    public List<List<Integer>> getLineIntegers(final String separator) throws IOException
    {
        return getLineIntegers(yearName, dayName, separator);
    }

    public List<List<Long>> getLineLongs(final String separator) throws IOException
    {
        return getLineLongs(yearName, dayName, separator);
    }

    public List<String> getLines() throws IOException
    {
        return getLines(yearName, dayName);
    }

    public List<List<String>> getLinesAsCharStrings() throws IOException
    {
        return getLinesAsCharStrings(yearName, dayName);
    }

    public CharMatrix getLinesAsCharMatrix(char defaultChar) throws IOException
    {
        return getLinesAsCharMatrix(yearName, dayName, defaultChar);
    }

    public IntMatrix getLinesAsIntMatrix(int defaultValue) throws IOException
    {
        return getLinesAsIntMatrix(yearName, dayName, defaultValue);
    }

    public List<List<Character>> getLinesAsChars() throws IOException
    {
        return getLinesAsChars(yearName, dayName);
    }

    public List<List<Integer>> getLinesAsDigits() throws IOException
    {
        return getLinesAsDigits(yearName, dayName);
    }

    public List<Integer> getLineAsInteger(final String separator) throws IOException
    {
        return getLineAsInteger(yearName, dayName, separator);
    }

    public List<Integer> getLinesAsInt() throws IOException
    {
        return getLinesAsInt(yearName, dayName);
    }

    public List<Long> getLinesAsLong() throws IOException
    {
        return getLinesAsLong(yearName, dayName);
    }

    public Map<String, List<String>> getLinesIntoStringMap(char partSeparator, char listSeparator) throws IOException
    {
        return getLinesIntoStringMap(yearName, dayName, partSeparator, listSeparator);
    }

    public Map<String, List<Integer>> getLinesIntoStringIntMap(char partSeparator, char listSeparator) throws IOException
    {
        return getLinesIntoStringIntMap(yearName, dayName, partSeparator, listSeparator);
    }

    public Map<String, List<Long>> getLinesIntoStringLongMap(char partSeparator, char listSeparator) throws IOException
    {
        return getLinesIntoStringLongMap(yearName, dayName, partSeparator, listSeparator);
    }
}
