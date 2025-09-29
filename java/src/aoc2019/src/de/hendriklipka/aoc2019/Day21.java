package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class Day21 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day21().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode robot = IntCode.fromIntList(code);

        // AND X Y -> Y = X AND Y
        // OR X Y -> Y = X OR Y
        // NOT X Y
        // A,B,C,D - true when ground at 1..4 steps ahead, F when it is a hole
        // T: temp, J: jump when true at the end
        // second register must be T or J, first can be any register
        List<String> program = List.of(
                "NOT A T\n", // check first ground
                "NOT B J\n", // check second ground
                "OR J T\n", // is one a hole?
                "NOT C J\n", // check third ground
                "OR T J\n", // is one of the first three a hole?
                "AND D J\n", // fourth must not be a hole - we would jump into it
                "WALK\n");


        // convert to a char list
        sendProgram(program, robot);
        final int[] lastValue = new int[1];
        printOutput(robot, lastValue);
        robot.execute();
        return lastValue[0];
    }

    private static void printOutput(final IntCode robot, final int[] lastValue)
    {
        robot.setDoOutput(value ->
        {
            if (value > 128)
            {
                lastValue[0] = value;
            }
            else
            {
                System.out.print((char) value.intValue());
            }
        });
    }

    private static void sendProgram(final List<String> program, final IntCode robot)
    {
        StringBuilder sb = new StringBuilder();
        for (String line : program)
            sb.append(line);
        char[] instructions = sb.toString().toCharArray();
        robot.setDoInput(new Supplier<>()
        {
            int ofs = 0;

            @Override
            public Integer get()
            {
                return (int) instructions[ofs++];
            }
        });
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode robot = IntCode.fromIntList(code);

        List<String> program = List.of(
                "NOT A T\n", // check first ground
                "NOT B J\n", // check second ground
                "OR J T\n", // is one of these a hole?
                "NOT C J\n", // check third ground
                "OR T J\n", // is one of the first three a hole?->we should jump
                "AND D J\n", // fourth must not be a hole - we would jump into it
                "NOT H T\n", // either H must be ground
                "NOT T T\n",
                "OR E T\n", // or E must be ground
                "AND T J\n", // only then we can jump (or we would jump into the H hole)
                "RUN\n");


        // convert to a char list
        sendProgram(program, robot);
        final int[] lastValue = new int[1];
        printOutput(robot, lastValue);
        robot.execute();
        return lastValue[0];
    }
}
