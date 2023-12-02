package de.hendriklipka.aoc2023.day02;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day02b
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocParseUtils.getLines("2023", "day02");
            List<Game> games = lines.stream().map(Game::new).toList();
            int sum=games.stream().mapToInt(Game::getPower).sum();
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
        private final List<List<CubeSet>> reveals = new ArrayList<>();

        public Game(String line)
        {
            final String[] p = line.split(":")[1].split(";");
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


        public int getPower()
        {
            int red=0;
            int green= 0;
            int blue= 0;
            for (List<CubeSet> reveal : reveals)
            {
                for (CubeSet cube : reveal)
                {
                    switch (cube.color)
                    {
                        case "red":
                            if (cube.count > red) red = cube.count;
                            break;
                        case "green":
                            if (cube.count > green) green = cube.count;
                            break;
                        case "blue":
                            if (cube.count > blue) blue = cube.count;
                            break;
                    }
                }
            }
            return red * green * blue;
        }
    }
}
