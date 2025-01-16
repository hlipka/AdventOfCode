package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day14 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day14().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        int recipeCount=data.getLinesAsInt().get(0);
        List<Integer> recipes=new ArrayList<>();
        recipes.add(3);
        recipes.add(7);
        int elf1=0;
        int elf2=1;
        while (recipes.size()<recipeCount+10)
        {
            int r1=recipes.get(elf1);
            int r2=recipes.get(elf2);
            char[] score = Integer.toString(r1 + r2).toCharArray();
            if (score.length==2)
            {
                recipes.add(score[0]-'0');
                recipes.add(score[1]-'0');
            }
            else
            {
                recipes.add(score[0] - '0');
            }
            elf1=(elf1+1+r1)%recipes.size();
            elf2=(elf2+1+r2)%recipes.size();
        }
        StringBuffer result= new StringBuffer();
        for (int i=0;i<10;i++)
        {
            result.append(recipes.get(i + recipeCount));
        }
        return result;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String pattern = data.getLines().get(0);
        List<Integer> recipes = new ArrayList<>();
        recipes.add(3);
        recipes.add(7);
        int elf1 = 0;
        int elf2 = 1;
        while (true)
        {
            int r1 = recipes.get(elf1);
            int r2 = recipes.get(elf2);
            char[] score = Integer.toString(r1 + r2).toCharArray();
            recipes.add(score[0] - '0');
            if (matches(recipes, pattern))
            {
                break;
            }
            if (score.length == 2)
            {
                recipes.add(score[1] - '0');
                if (matches(recipes, pattern))
                {
                    break;
                }
            }
            elf1 = (elf1 + 1 + r1) % recipes.size();
            elf2 = (elf2 + 1 + r2) % recipes.size();
        }
        return recipes.size()-pattern.length();
    }

    private boolean matches(List<Integer> compare, String pattern)
    {
        if (compare.size()<pattern.length())
        {
            return false;
        }
        int offset=compare.size()-pattern.length();
        for (int i=0;i<pattern.length();i++)
        {
            if (compare.get(i+offset)!=(pattern.charAt(i)-'0'))
            {
                return false;
            }
        }
        return true;
    }
}
