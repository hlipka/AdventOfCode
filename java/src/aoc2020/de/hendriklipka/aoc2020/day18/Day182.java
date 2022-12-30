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
public class Day182
{
    public static void main(String[] args)
    {
        try
        {
            long result=FileUtils.readLines(new File("../data/2020/day18.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Day182::calculate).reduce(0L, Long::sum);
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
            List<Object> st = Collections.list(new StringTokenizer(line, " ()", true));
            List<String> tokens = st.stream().map(s->(String)s).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            final long result = evaluate(tokens.iterator());
            System.out.println("evaluated ["+line+"] into "+result);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private static long evaluate(Iterator<String> tokens) throws IOException
    {
        Stack<String> stack=new Stack<>();
        long current = 0;
        while (tokens.hasNext())
        {
            String nextToken = tokens.next();
            if (nextToken.equals("("))
            {
                stack.push(Long.toString(evaluate(tokens)));
            }
            else if (Character.isDigit(nextToken.charAt(0)))
            {
                stack.push(nextToken);
            }
            else if (nextToken.equals(")"))
            {
                break;
            }
            else // operator
            {
               char operator = nextToken.charAt(0);
               if (operator=='+')
               {
                   stack.push(nextToken);
               }
               else if (stack.size()<3)
               {
                   stack.push(nextToken);
               }
               else
               {
                   while(stack.size()>1 && stack.elementAt(stack.size()-2).equals("+"))
                   {
                       String op1 = stack.pop();
                       stack.pop();
                       String op2 = stack.pop();
                       long l = operate(Long.parseLong(op1), Long.parseLong(op2), '+');
                       stack.push(Long.toString(l));
                   }
                   stack.push(nextToken);
               }
            }
        }
        while (stack.size()!=1)
        {
            String op1 = stack.pop();
            String operator = stack.pop();
            String op2 = stack.pop();
            long l = operate(Long.parseLong(op1), Long.parseLong(op2), operator.charAt(0));
            stack.push(Long.toString(l));
        }

        return Long.parseLong(stack.pop());
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
