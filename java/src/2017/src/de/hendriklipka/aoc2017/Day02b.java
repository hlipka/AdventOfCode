package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * User: hli
 */
public class Day02b
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<Integer>> sheet = AocParseUtils.getLineIntegers("2017", "day02", "\t");
            int sum = sheet.stream().mapToInt(Day02b::getDiff).sum();
            System.out.println(sum  );
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int getDiff(List<Integer> row)
    {
        Collections.sort(row); // ascending
        for (int pos =0; pos < row.size()-1; pos++)
        {
            int thisNum=row.get(pos);
            for (int other = pos + 1; other < row.size(); other++)
            {
                int otherNum = row.get(other);
                if (0==(otherNum % thisNum))
                {
                    return otherNum / thisNum;
                }
            }
        }
        throw new IllegalArgumentException("Cannot found divisors in " + StringUtils.join(row,","));
    }
}
