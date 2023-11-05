package de.hendriklipka.aoc2016.day02;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 04.11.23
 * Time: 23:19
 */
public class Day02b
{
    public static void main(String[] args) throws IOException
    {
        final List<List<String>> instructions = AocParseUtils.getLinesAsChars("2016", "day02");
        String code = "";
        char button = '5';
        for (List<String> line : instructions)
        {
            for (String move : line)
            {
                button = move(button, move);
            }
            code += button;
        }
        System.out.println(code);
    }

    private static char move(final char button, final String move)
    {
        return switch(button)
        {
            case '1'->switch(move)
            {
                case "U"->'1';
                case "D"->'3';
                case "L"->'1';
                case "R"->'1';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '2' -> switch (move)
            {
                case "U" -> '2';
                case "D" -> '6';
                case "L" -> '2';
                case "R" -> '3';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '3' -> switch (move)
            {
                case "U" -> '1';
                case "D" -> '7';
                case "L" -> '2';
                case "R" -> '4';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '4' -> switch (move)
            {
                case "U" -> '4';
                case "D" -> '8';
                case "L" -> '3';
                case "R" -> '4';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '5' -> switch (move)
            {
                case "U" -> '5';
                case "D" -> '5';
                case "L" -> '5';
                case "R" -> '6';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '6' -> switch (move)
            {
                case "U" -> '2';
                case "D" -> 'A';
                case "L" -> '5';
                case "R" -> '7';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '7' -> switch (move)
            {
                case "U" -> '3';
                case "D" -> 'B';
                case "L" -> '6';
                case "R" -> '8';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '8' -> switch (move)
            {
                case "U" -> '4';
                case "D" -> 'C';
                case "L" -> '7';
                case "R" -> '9';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case '9' -> switch (move)
            {
                case "U" -> '9';
                case "D" -> '9';
                case "L" -> '8';
                case "R" -> '9';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case 'A' -> switch (move)
            {
                case "U" -> '6';
                case "D" -> 'A';
                case "L" -> 'A';
                case "R" -> 'B';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case 'B' -> switch (move)
            {
                case "U" -> '7';
                case "D" -> 'D';
                case "L" -> 'A';
                case "R" -> 'C';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case 'C' -> switch (move)
            {
                case "U" -> '8';
                case "D" -> 'C';
                case "L" -> 'B';
                case "R" -> 'C';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            case 'D' -> switch (move)
            {
                case "U" -> 'B';
                case "D" -> 'D';
                case "L" -> 'D';
                case "R" -> 'D';
                default -> throw new IllegalStateException("Unexpected value: " + move);
            };
            default -> throw new IllegalStateException("Unexpected value: " + button);
        };
    }
}
