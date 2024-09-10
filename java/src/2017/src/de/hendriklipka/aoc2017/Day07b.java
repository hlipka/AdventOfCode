package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: hli
 */
public class Day07b
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocParseUtils.getLines("2017", "day07");
            final List<Program> programs = lines.stream().map(Day07b::parseProgram).toList();
            Set<String> supported = new HashSet<>();
            Map<String, Program> names = programs.stream().collect(Collectors.toMap(p->p.name, p->p));
            programs.forEach(program -> supported.addAll(program.supported));
            Program root = programs.stream().filter(p->!supported.contains(p.name)).findAny().orElseThrow();
            System.out.println(root.name);
            root.calculate(names);
            System.out.println(root.totalWeight);
            Program next = findUnequal(root, names);
            if (next == null)
            {
                throw new IllegalArgumentException("Only matches in the first level");
            }
            // find weight difference on the root program
            Program other;
            if (root.supported.get(0).equals(next.name))
            {
                other = names.get(root.supported.get(1));
            }
            else
            {
                other = names.get(root.supported.get(0));
            }
            int diff=other.totalWeight - next.totalWeight;
            // recursively look through the wrong program until all children are correct
            while (true)
            {
                Program wrong = findUnequal(next, names);
                if (null!=wrong)
                {
                    next = wrong;
                }
                else
                {
                    break;
                }
            }

            // then
            System.out.println(next.weight+diff);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private static Program findUnequal(final Program root, Map<String, Program> names)
    {
        if (root.supported.size()<2)
        {
            return null; // these children must be correct
        }
        if (root.supported.size() == 2 && (names.get(root.supported.get(0)).totalWeight != names.get(root.supported.get(1)).totalWeight))
        {
            throw new IllegalArgumentException("only two discs of unequal weight in "+root.name+" - cannot decide!");
        }
        // first two are equal? Then look for the one which is different (when all are the same, return null)
        if (names.get(root.supported.get(0)).totalWeight == names.get(root.supported.get(1)).totalWeight)
        {
            final int correct = names.get(root.supported.get(0)).totalWeight;
            return root.supported.stream().skip(2).map(names::get).filter(p-> p.totalWeight != correct).findAny().orElse(null);
        }
        // the first two are different -> see whether at least one also matches the first -> of so the second is the wrong one
        final int compare = names.get(root.supported.get(0)).totalWeight;
        if (root.supported.stream().skip(2).map(names::get).anyMatch(p -> p.totalWeight == compare))
        {
            return names.get(root.supported.get(1));
        }
        // if nothing matches the first one, it is the wrong one
        return names.get(root.supported.get(0));
    }

    private static Program parseProgram(String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "^([a-z]*)\\s+\\((\\d+)\\)(.*)");
        Program program = new Program(parts.get(0), Integer.parseInt(parts.get(1)));
        if (parts.size()>2 && !parts.get(2).trim().isEmpty())
        {
            final String[] discs = StringUtils.split(parts.get(2).substring(4), ", ");
            program.setSupported(Arrays.asList(discs));
        }
        return program;
    }

    private static class Program
    {
        String name;
        int weight;
        int totalWeight = -1;
        List<String> supported = new ArrayList<>();

        public Program(final String name, final int weight)
        {
            this.name = name;
            this.weight = weight;
        }

        public void setSupported(final List<String> list)
        {
            supported = list;
        }

        public int calculate(final Map<String, Program> names)
        {
            totalWeight = weight + supported.stream().mapToInt(p -> names.get(p).calculate(names)).sum();
            return totalWeight;
        }

        @Override
        public String toString()
        {
            return "Program{" +
                   "name='" + name + '\'' +
                   ", weight=" + weight +
                   ", totalWeight=" + totalWeight +
                   ", supported=" + StringUtils.join(supported,", ") +
                   '}';
        }
    }
}
