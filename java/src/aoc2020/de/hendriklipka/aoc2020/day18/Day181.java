package de.hendriklipka.aoc2020.day18;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 18.12.20
 * Time: 20:22
 */
public class Day181
{
    public static void main(String[] args)
    {
        try
        {
            long result=FileUtils.readLines(new File("data/day18.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Day181::calculate).reduce(0L, Long::sum);
            System.out.println(result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static long calculate(final String line)
    {
        try
        {
            List<String> tokens = Collections.list(new StringTokenizer(line, " ()", true)).stream().map(s->(String)s).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            return evaluate(tokens.iterator());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private static long evaluate(Iterator<String> tokens) throws IOException
    {
        long current = 0;
        char operator=' ';
        while (tokens.hasNext())
        {
            String nextToken = tokens.next();
            if (nextToken.equals("("))
            {
                current = operate(current, evaluate(tokens), operator);
            }
            else if (Character.isDigit(nextToken.charAt(0)))
            {
                current = operate(current, Long.parseLong(nextToken), operator);
            }
            else if (nextToken.equals(")"))
            {
                return current;
            }
            else // operator
            {
                operator = nextToken.charAt(0);
            }
        }
        return current;
    }

    private static long operate(final long op1, final long op2, final char operator)
    {
        switch(operator)
        {
            case ' ': return op2;
            case '+': return op1+op2;
            case '*': return op1*op2;
        }
        throw new IllegalArgumentException("invalid operator "+operator);
    }
}
