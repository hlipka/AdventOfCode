package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day05 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day05().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        String polymer = data.getLines().get(0);
        return reactPolymer(polymer);
    }

    private static int reactPolymer(String polymer)
    {
        while (true)
        {
            // we walk once through the current polymer and do all reactions - that way we do not constantly change the current string
            StringBuilder next=new StringBuilder();
            char[] p= polymer.toCharArray();
            for (int i=0;i<p.length;i++)
            {
                char c1=p[i];
                if (i==p.length-1)
                {
                    next.append(c1);
                }
                else
                {
                    char c2 = p[i + 1];
                    if (Character.toUpperCase(c1) == Character.toUpperCase(c2) && c1!=c2)
                    {
                        i++;
                    }
                    else
                    {
                        next.append(c1);
                    }
                }
            }
            // do this as long as there are no more reactions
            if (next.length() == polymer.length())
                return next.length();
            polymer =next.toString();
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String polymer = data.getLines().get(0);
        // find all existing characters
        Set<Character> allChars = new HashSet<>( );
        for (char c : polymer.toCharArray())
            allChars.add(Character.toLowerCase(c));
        // remove each character pair, do the reaction and find the minimum
        return allChars.stream().map(c->
                polymer.replace("" + c, "").replace("" + Character.toUpperCase(c), "")).mapToInt(Day05::reactPolymer).min().orElseThrow();
    }
}
