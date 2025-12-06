package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day06 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day06().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> tasks = data.getLineWords(" ");
        int taskCount=tasks.getFirst().size();
        long sum=0;
        for (int i=0; i<taskCount; i++)
        {
            sum+=doTask(tasks, i);
        }
        return sum;
    }

    private long doTask(final List<List<String>> tasks, final int task)
    {
        String op=tasks.getLast().get(task);
        if (op.equals("+"))
            return doSum(tasks, task);
        return doProduct(tasks, task);
    }

    private long doProduct(final List<List<String>> tasks, final int task)
    {
        long prod = 1;
        for (int i = 0; i < tasks.size() - 1; i++)
        {
            prod *= Integer.parseInt(tasks.get(i).get(task));
        }
        return prod;

    }

    private long doSum(final List<List<String>> tasks, final int task)
    {
        long sum=0;
        for (int i=0;i<tasks.size()-1;i++)
        {
            sum+=Integer.parseInt(tasks.get(i).get(task));
        }
        return sum;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final CharMatrix tasks = data.getLinesAsCharMatrix(' ');
        int opRow=tasks.rows()-1;
        long sum=0;
        char op='+';
        List<Long> nums=new ArrayList<>();
        // run through all columns
        for (int i=0;i<tasks.cols();i++)
        {
            // get the current operator for the task
            if (' ' != tasks.at(opRow, i))
            {
                op = tasks.at(opRow, i);
            }
            // an empty column means we have finished a task
            if (columnIsEmpty(tasks, i))
            {
                sum+= calculate(op, nums);
                nums.clear();
            }
            // collect numbers from non-empty columns
            else
            {
                nums.add(getNum(tasks, i));
            }
        }
        // also add the last task
        sum += calculate(op, nums);
        return sum;
    }

    private static long calculate(final char op, final List<Long> nums)
    {
        return switch (op)
        {
            case '+' -> nums.stream().reduce(0L, Long::sum);
            case '*' -> nums.stream().reduce(1L, (a, b) -> a * b);
            default -> throw new IllegalArgumentException("found [" + op + "]");
        };
    }

    private Long getNum(final CharMatrix tasks, final int i)
    {
        String s=String.valueOf(tasks.column(i));
        return Long.parseLong(s.substring(0,s.length()-1).trim());
    }

    private boolean columnIsEmpty(final CharMatrix tasks, final int i)
    {
        return tasks.countInCol(i, ' ') == tasks.rows();
    }
}
