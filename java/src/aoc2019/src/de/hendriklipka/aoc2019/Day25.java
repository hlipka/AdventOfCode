package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class Day25 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day25().doPuzzle(args);
    }

    public StringBuilder buffer = new StringBuilder();

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        BigIntCode intCode = BigIntCode.fromStringList(code);
        intCode.setDoOutput(this::printASCII);
        // Just play manually. Be careful with what you pick up.
        intCode.setDoInput(new KeyboardInput());
        intCode.execute();
        return -1;
    }

    private void printASCII(BigInteger value)
    {
        char c = (char) value.intValue();
        if (c != 0x0a)
        {
            buffer.append(c);
        }
        else
        {
            System.out.println(buffer.toString());
            buffer = new StringBuilder();
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }

    private static class KeyboardInput implements Supplier<BigInteger>
    {
        List<Character> line = new ArrayList<>();

        @Override
        public BigInteger get()
        {
            if (!line.isEmpty())
            {
                int c = line.removeFirst();
                return BigInteger.valueOf(c);
            }
            System.out.print("> ");
            Scanner scan = new Scanner(System.in);
            String text = scan.nextLine();
            for (char c : text.toCharArray())
            {
                line.add(c);
            }
            line.add((char) 0x0a);
            return get();
        }
    }
}
