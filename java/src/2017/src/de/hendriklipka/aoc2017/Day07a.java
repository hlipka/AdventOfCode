package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 */
public class Day07a
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocDataFileUtils.getLines("2017", "day07");
            final List<Program> programs = lines.stream().map(Day07a::parseProgram).toList();
            Set<String> supported = new HashSet<>();
            programs.forEach(program -> supported.addAll(program.supported));
            programs.stream().filter(p->!supported.contains(p.name)).forEach(x -> System.out.println(x.name));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private static Program parseProgram(String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "^([a-z]*)\\s+\\((\\d+)\\)(.*)");
        Program program = new Program(parts.get(0), Integer.parseInt(parts.get(1)));
        if (parts.size()>2)
        {
            final String[] discs = StringUtils.split(parts.get(2), ", ");
            program.setSupported(Arrays.asList(discs));
        }
        return program;
    }

    private static class Program
    {
        String name;
        int weight;
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
    }
}
