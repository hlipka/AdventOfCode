package de.hendriklipka.aoc2024.day03;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03b
{
    public static void main(String[] args)
    {
        try
        {
            long result = 0;
            boolean enabled=true;
            String line = StringUtils.join(AocParseUtils.getLines("2024", "day03"));
            Pattern p = Pattern.compile("(mul\\((\\d{1,3}+),(\\d{1,3}+)\\))|(do\\(\\))|(don't\\(\\))");
            Matcher m = p.matcher(line);
            while(m.find())
            {
                String instr=m.group(0);
                if (instr.startsWith("mul(") && enabled)
                {
                    String g1 = m.group(2);
                    String g2 = m.group(3);
                    result += Integer.parseInt(g1) * Integer.parseInt(g2);
                }
                else if (instr.startsWith("do("))
                {
                    enabled = true;
                }
                else
                {
                    enabled = false;
                }
            }
            System.out.println(result);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
