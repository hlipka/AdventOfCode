package de.hendriklipka.aoc2016.day06;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 20:37
 */
public class Day06a
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> lines = AocParseUtils.getLinesAsCharStrings("2016", "day06");
            int colCount=lines.get(0).size();
            List<Map<String,Integer>> frequenciesPerColumn = new ArrayList<>(10);
            for (int i=0;i<colCount;i++)
            {
                frequenciesPerColumn.add(new HashMap<>());
            }
            for (List<String> line: lines)
            {
                for (int i=0;i<colCount;i++)
                {
                    final Map<String, Integer> colData = frequenciesPerColumn.get(i);
                    String c = line.get(i);
                    Integer count=colData.getOrDefault(c, 0);
                    colData.put(c, count-1); // going negative, so that the sorting below has the most frequent character first
                }
            }
            List<String> most=frequenciesPerColumn.stream().map(
                    f->f.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue))
                            .map(Map.Entry::getKey).findFirst().orElseThrow()
            ).toList();
            System.out.println(StringUtils.join(most, ""));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
