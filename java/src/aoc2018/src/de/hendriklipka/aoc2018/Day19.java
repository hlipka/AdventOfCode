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
        runComputer(m);
        return m.getRegs().get(0);
    }

    private void runComputer(final Day16.Machine m) throws IOException
    {
        final List<String> programLines = data.getLines();
        String pcRegDef=programLines.get(0);
        int pcReg=AocParseUtils.parseIntFromString(pcRegDef, "#ip (\\d+)");
        programLines.remove(0);
        List<Pair<String, List<Integer>>> program = new ArrayList<>();
        for (String line: programLines)
        {
            final List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+) (\\d+) (\\d+) (\\d+)");
            String cmd=parts.get(0);
            parts.remove(0);
            List<Integer> command = new ArrayList<>(parts.stream().map(Integer::parseInt).toList());
            program.add(Pair.of(cmd, command));
        }
        int pc=0;
        while (true)
        {
            m.getRegs().set(pcReg, pc);
            final Pair<String, List<Integer>> step = program.get(pc);
            m.runCommand(step.getLeft(), step.getRight().get(0), step.getRight().get(1), step.getRight().get(2));
            pc= m.getRegs().get(pcReg);
            pc++;
            if (pc>=program.size() || pc<0)
                break;

        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        Day16.Machine m=new Day16.Machine(new ArrayList<>(List.of(1, 0, 0, 0, 0, 0)));
        runComputer(m);
        return m.getRegs().get(0);
    }
}
