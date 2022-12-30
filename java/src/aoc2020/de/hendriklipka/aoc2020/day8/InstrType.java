package de.hendriklipka.aoc2020.day8;

/**
 * User: hli
 * Date: 08.12.20
 * Time: 20:42
 */
public enum InstrType
{
    NOP, JMP,ACC;

    public static InstrType fromType(String type)
    {
        switch(type)
        {
            case "nop": return NOP;
            case "acc": return ACC;
            case "jmp": return JMP;
        }
        return null;
    }
}
