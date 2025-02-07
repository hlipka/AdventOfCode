package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day16().doPuzzle(args);
    }
    final static String[] COMMANDS={"addr","addi","mulr","muli","banr","bani","borr","bori","setr","seti","gtir","gtri","gtrr","eqir","eqri","eqrr"};

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        int count=0;
        for (List<String> block : blocks)
        {
            if (block.isEmpty())
                continue;
            if (block.get(0).startsWith("Before"))
            {
                List<Integer> startRegs=
                        AocParseUtils.parsePartsFromString(block.get(0), "Before: \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]").stream().map(Integer::parseInt).toList();
                List<Integer> command=
                        new ArrayList<>(AocParseUtils.parsePartsFromString(block.get(1), "(\\d+) (\\d+) (\\d+) (\\d+)").stream().map(Integer::parseInt).toList());
                List<Integer> endRegs=
                        AocParseUtils.parsePartsFromString(block.get(2), "After:  \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]").stream().map(Integer::parseInt).toList();
                int matchCount= (int) IntStream.range(0, 16).filter(i -> commandMatchesExample(startRegs, COMMANDS[i], command, endRegs)).count();
                if (matchCount>=3)
                    count++;
            }
        }
        return count;
    }

    private static boolean commandMatchesExample(final List<Integer> startRegs, String cmd, final List<Integer> command, final List<Integer> endRegs)
    {
        Machine m=new Machine(new ArrayList<>(startRegs));
        m.runCommand(cmd, command.get(1), command.get(2), command.get(3));
        return m.getRegs().equals(endRegs);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        Map<Integer, List<String>> commandMap=new HashMap<>();
        for (int i = 0; i < 16; i++)
        {
            commandMap.put(i, new ArrayList<>(Arrays.asList(COMMANDS)));
        }
        for (List<String> block : blocks)
        {
            if (block.isEmpty())
                continue;
            if (block.get(0).startsWith("Before"))
            {
                List<Integer> startRegs =
                        AocParseUtils.parsePartsFromString(block.get(0), "Before: \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]").stream().map(
                                Integer::parseInt).toList();
                List<Integer> command =
                        new ArrayList<>(
                                AocParseUtils.parsePartsFromString(block.get(1), "(\\d+) (\\d+) (\\d+) (\\d+)").stream().map(Integer::parseInt).toList());
                List<Integer> endRegs =
                        AocParseUtils.parsePartsFromString(block.get(2), "After:  \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]").stream().map(
                                Integer::parseInt).toList();
                final List<String> potentialCommands = commandMap.get(command.get(0));
                List<String> matching= potentialCommands.stream().filter(cmd -> commandMatchesExample(startRegs, cmd, command, endRegs)).collect(
                        Collectors.toList());
                commandMap.put(command.get(0), matching);
                // when were able to match one instruction, remove it from all other lists
                if (matching.size()==1)
                {
                    String cmd=matching.get(0);
                    commandMap.values().stream().filter(list -> 1 != list.size()).forEach(list -> list.remove(cmd));
                }
            }
            else
            {
                Machine m=new Machine(new ArrayList<>(List.of(0, 0, 0, 0)));
                for (String line: block)
                {
                    List<Integer> command =
                            new ArrayList<>(
                                    AocParseUtils.parsePartsFromString(line, "(\\d+) (\\d+) (\\d+) (\\d+)").stream().map(Integer::parseInt).toList());
                    m.runCommand(commandMap.get(command.get(0)).get(0), command.get(1), command.get(2), command.get(3));
                }
                return m.getRegs().get(0);
            }
        }
        return -1;
    }

    public static class Machine
    {
        private final List<Integer> regs;

        public Machine(final List<Integer> regs)
        {
            this.regs=regs;
        }

        public List<Integer> getRegs()
        {
            return regs;
        }

        public void runCommand(final String command, final Integer argA, final Integer argB, final Integer argC)
        {
            switch(command)
            {
                case "addr": regs.set(argC, regs.get(argA) + regs.get(argB)); break;
                case "addi": regs.set(argC, regs.get(argA) + argB); break;
                case "mulr": regs.set(argC, regs.get(argA) * regs.get(argB)); break;
                case "muli": regs.set(argC, regs.get(argA) * argB); break;
                case "banr": regs.set(argC, regs.get(argA) & regs.get(argB)); break;
                case "bani": regs.set(argC, regs.get(argA) & argB); break;
                case "borr": regs.set(argC, regs.get(argA) | regs.get(argB)); break;
                case "bori": regs.set(argC, regs.get(argA) | argB); break;
                case "setr": regs.set(argC, regs.get(argA)); break;
                case "seti": regs.set(argC, argA); break;
                case "gtir": regs.set(argC, argA > regs.get(argB)?1:0); break;
                case "gtri":
                    regs.set(argC, regs.get(argA) > argB ? 1 : 0);
                    break;
                case "gtrr":
                    regs.set(argC, regs.get(argA) > regs.get(argB) ? 1 : 0);
                    break;
                case "eqir":
                    regs.set(argC, argA.equals(regs.get(argB)) ? 1 : 0);
                    break;
                case "eqri":
                    regs.set(argC, regs.get(argA).equals(argB) ? 1 : 0);
                    break;
                case "eqrr":
                    regs.set(argC, regs.get(argA).equals(regs.get(argB)) ? 1 : 0);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command: "+command);

            }
        }
    }
}
