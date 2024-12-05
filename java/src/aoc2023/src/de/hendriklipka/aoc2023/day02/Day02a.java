package de.hendriklipka.aoc2023.day02;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day02a
{
    final static int RED = 12;
    final static int GREEN = 13;
    final static int BLUE = 14;
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocDataFileUtils.getLines("2023", "day02");
            List<Game> games = lines.stream().map(Game::new).toList();
            int sum=games.stream().filter(Game::isPossible).mapToInt(Game::getNum).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    static class CubeSet
    {
        int count;
        String color;

        public CubeSet(String line)
        {
            count = AocParseUtils.parseIntFromString(line, " (\\d+) \\w+");
            color = AocParseUtils.parseStringFromString(line, " \\d+ (\\w+)");
        }

        @Override
        public String toString()
        {
            return "CubeSet{" +
                    "count=" + count +
                    ", color='" + color + '\'' +
                    '}';
        }
    }
    static class Game
    {
        private final int num;
        private final List<List<CubeSet>> reveals = new ArrayList<>();

        public Game(String line)
        {
            final String[] parts = line.split(":");
            num = AocParseUtils.parseIntFromString(parts[0], "Game (\\d+)");
            final String[] p = parts[1].split(";");
            for (String reveal: p)
            {
                List<CubeSet> cubes = new ArrayList<>();
                reveals.add(cubes);
                String[] cubeDesc = reveal.split(",");
                for (String cube: cubeDesc)
                {
                    cubes.add(new CubeSet(cube));
                }
            }
        }

        public boolean isPossible()
        {
            for (List<CubeSet> reveal: reveals)
            {
                for (CubeSet cube: reveal)
                {
                    switch (cube.color)
                    {
                        case "red": if (cube.count>RED) return false;
                        break;
                        case "green": if (cube.count>GREEN) return false;
                        break;
                        case "blue": if (cube.count>BLUE) return false;
                        break;
                    }
                }
            }
            return true;
        }

        public int getNum()
        {
            return num;
        }
    }
}
