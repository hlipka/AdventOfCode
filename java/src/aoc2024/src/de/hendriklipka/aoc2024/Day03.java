package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day03().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        long result = 0;
        String line = StringUtils.join(data.getLines());
        Pattern p = Pattern.compile("mul\\((\\d{1,3}+),(\\d{1,3}+)\\)");
        Matcher m = p.matcher(line);
        while(m.find())
        {
            String g1=m.group(1);
            String g2=m.group(2);
            result+=Long.parseLong(g1)*Long.parseLong(g2);
        }
        return result;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        long result = 0;
        boolean enabled=true;
        String line = StringUtils.join(data.getLines());
        Pattern p = Pattern.compile("(mul\\((\\d{1,3}+),(\\d{1,3}+)\\))|(do\\(\\))|(don't\\(\\))");
        Matcher m = p.matcher(line);
        while(m.find())
        {
            String instr=m.group(0);
            if (instr.startsWith("mul(") && enabled)
            {
                String g1 = m.group(2);
                String g2 = m.group(3);
                result += Long.parseLong(g1) * Long.parseLong(g2);
            }
            else enabled = instr.startsWith("do(");
        }
        return result;
    }
}
