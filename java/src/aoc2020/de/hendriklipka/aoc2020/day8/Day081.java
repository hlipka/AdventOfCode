package de.hendriklipka.aoc2020.day8;

import de.hendriklipka.aoc2020.day7.Day072;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: hli
 * Date: 08.12.20
 * Time: 20:39
 */
public class Day081
{
    static int acc;
    static int ip;
    static List<Instruction> instructions=new ArrayList<>();

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("data/day8.txt"), StandardCharsets.UTF_8).forEach(Day081::parseInstr);
            runCode();
            System.out.println(acc);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void runCode()
    {
        acc=0;
        ip=0;
        boolean[] visited=new boolean[instructions.size()];
        while (!visited[ip])
        {
            Instruction next=instructions.get(ip);
            visited[ip]=true;
            switch (next.type)
            {
                case NOP:
                    ip++;
                    break;
                case JMP:
                    ip+=next.arg;
                    break;
                case ACC:
                    acc+=next.arg;
                    ip++;
                    break;
            }
        }
    }

    private static void parseInstr(final String line)
    {
        String instrStr=line.substring(0,3);
        String argStr=line.substring(4);
        int arg=Integer.parseInt(argStr);
        Instruction inst = new Instruction(InstrType.fromType(instrStr.toLowerCase(Locale.ROOT)), arg);
        instructions.add(inst);
    }

    private static class Instruction
    {
        public InstrType type;
        public int arg;

        public Instruction(final InstrType type, final int arg)
        {
            this.type = type;
            this.arg = arg;
        }
    }
}
