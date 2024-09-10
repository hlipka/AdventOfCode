package de.hendriklipka.aoc;

import java.util.Arrays;

/**
 * User: hli
 */
public class AocStringUtils
{
    public static String sortWord(String word)
    {
        final var chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
