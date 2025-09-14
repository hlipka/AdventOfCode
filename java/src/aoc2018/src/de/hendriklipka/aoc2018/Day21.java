package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day21 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day21().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        Day16.Machine m = new Day16.Machine(new ArrayList<>(List.of(0, 0, 0, 0, 0, 0)));
        final List<String> programLines = data.getLines();
        String pcRegDef = programLines.get(0);
        int pcReg = AocParseUtils.parseIntFromString(pcRegDef, "#ip (\\d+)");
        programLines.remove(0);
        final var program = parseProgram(programLines);
        int pc = 0;
        do
        {
            m.getRegs().set(pcReg, pc);
            final Pair<String, List<Integer>> step = program.get(pc);
            if (pc==28)
            {
                // print out the compare value - when this is met we would halt the program
                return m.getRegs().get(4);
            }
            m.runCommand(step.getLeft(), step.getRight().get(0), step.getRight().get(1), step.getRight().get(2));
            pc = m.getRegs().get(pcReg);
            pc++;
        } while (pc < program.size() && pc >= 0);
        return m.getRegs().get(0);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        Set<Integer> values=new HashSet<>();
        int lastValue=0;
        Day16.Machine m = new Day16.Machine(new ArrayList<>(List.of(0, 0, 0, 0, 0, 0)));
        final List<String> programLines = data.getLines();
        String pcRegDef = programLines.get(0);
        int pcReg = AocParseUtils.parseIntFromString(pcRegDef, "#ip (\\d+)");
        programLines.remove(0);
        final var program = parseProgram(programLines);
        int pc = 0;
        do
        {
            m.getRegs().set(pcReg, pc);
            final Pair<String, List<Integer>> step = program.get(pc);
            if (pc == 28)
            {
                // get the compare value - when this is met we would halt the program
                final var value = m.getRegs().get(4);
                // assuming that the program goes into a loop at one point, the value we want is the compare value right
                // before we detect the loop (the detected value is the first one we would hit at an earlier iteration)
                if (values.contains(value))
                {
                    return lastValue;
                }
                values.add(value);
                lastValue=value;
            }
            m.runCommand(step.getLeft(), step.getRight().get(0), step.getRight().get(1), step.getRight().get(2));
            pc = m.getRegs().get(pcReg);
            pc++;
        } while (pc < program.size() && pc >= 0);
        return m.getRegs().get(0);
    }

    private static List<Pair<String, List<Integer>>> parseProgram(final List<String> programLines)
    {
        List<Pair<String, List<Integer>>> program = new ArrayList<>();
        for (String line : programLines)
        {
            final List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+) (\\d+) (\\d+) (\\d+)");
            String cmd = parts.get(0);
            parts.remove(0);
            List<Integer> command = new ArrayList<>(parts.stream().map(Integer::parseInt).toList());
            program.add(Pair.of(cmd, command));
        }
        return program;
    }
}
