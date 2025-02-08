package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day19 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day19().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        Day16.Machine m=new Day16.Machine(new ArrayList<>(List.of(0, 0, 0, 0, 0, 0)));
        final List<String> programLines = data.getLines();
        String pcRegDef=programLines.get(0);
        int pcReg=AocParseUtils.parseIntFromString(pcRegDef, "#ip (\\d+)");
        programLines.remove(0);
        final var program = parseProgram(programLines);
        int pc=0;
        do
        {
            m.getRegs().set(pcReg, pc);
            final Pair<String, List<Integer>> step = program.get(pc);
            m.runCommand(step.getLeft(), step.getRight().get(0), step.getRight().get(1), step.getRight().get(2));
            pc = m.getRegs().get(pcReg);
            pc++;
        } while (pc < program.size() && pc >= 0);
        return m.getRegs().get(0);
    }

    private static List<Pair<String, List<Integer>>> parseProgram(final List<String> programLines)
    {
        List<Pair<String, List<Integer>>> program = new ArrayList<>();
        for (String line: programLines)
        {
            final List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+) (\\d+) (\\d+) (\\d+)");
            String cmd=parts.get(0);
            parts.remove(0);
            List<Integer> command = new ArrayList<>(parts.stream().map(Integer::parseInt).toList());
            program.add(Pair.of(cmd, command));
        }
        return program;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        Day16.Machine m=new Day16.Machine(new ArrayList<>(List.of(1, 0, 0, 0, 0, 0)));
        final List<String> programLines = data.getLines();
        String pcRegDef=programLines.get(0);
        int pcReg=AocParseUtils.parseIntFromString(pcRegDef, "#ip (\\d+)");
        programLines.remove(0);
        final var program = parseProgram(programLines);
        int pc=0;
        do
        {
            m.getRegs().set(pcReg, pc);
            final Pair<String, List<Integer>> step = program.get(pc);
            m.runCommand(step.getLeft(), step.getRight().get(0), step.getRight().get(1), step.getRight().get(2));
            pc = m.getRegs().get(pcReg);
            pc++;
            // we need to run until we are at PC=1, since then the target number was calculated
        } while (pc != 1);
        int target=m.getRegs().get(4);
        int result=0;
        // find all factors of the target and sum them
        for (int i=1;i<=    target;i++)
        {
            if (0==(target%i))
                result+=i;
        }
        return result;
    }
}
