package de.hendriklipka.aoc2020.day17;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day172
{
    private static final char INACTIVE = '.';
    private static final char ACTIVE = '#';

    private static final int ITERATIONS = 6;

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day17.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            int rows = lines.size();
            int cols = lines.get(0).length();
            int totalY = rows + 2 * ITERATIONS;
            int totalX = cols + 2 * ITERATIONS;
            int totalZ = 1 + 2 * ITERATIONS;
            int totalW = 1 + 2 * ITERATIONS;
            char[] cubes = new char[totalY * totalX * totalZ * totalW];
            Arrays.fill(cubes, INACTIVE);
            int currentY = ITERATIONS;
            for (String line : lines)
            {
                parseLine(cubes, line, ITERATIONS, currentY++, ITERATIONS, ITERATIONS, totalX, totalY, totalZ, totalW);
            }

            for (int iteration = 0; iteration < ITERATIONS; iteration++)
            {
                cubes = updateCubes(cubes, totalX, totalY, totalZ, totalW);
            }
            System.out.println(countCubes(cubes));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static char[] updateCubes(char[] cubes, int totalX, int totalY, int totalZ, int totalW)
    {
        char[] newCubes = new char[cubes.length];
        for (int w = 0; w < totalW; w++)
        {
            for (int z = 0; z < totalZ; z++)
            {
                for (int x = 0; x < totalX; x++)
                {
                    for (int y = 0; y < totalY; y++)
                    {
                        newCubes[getAddress(x, y, z, w, totalX, totalY, totalZ, totalW)] = getNewCube(cubes, x, y, z, w, totalX, totalY, totalZ, totalW);
                    }
                }
            }
        }
        return newCubes;
    }

    private static char getNewCube(char[] cubes, int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW)
    {
        char c = cubes[getAddress(x, y, z, w, totalX, totalY, totalZ, totalW)];
        int n = countNeighbours(cubes, x, y, z, w, totalX, totalY, totalZ, totalW);
        if (c == ACTIVE && (n < 2 || n > 3))
        {
            return INACTIVE;
        }
        if (c == INACTIVE && n == 3)
        {
            return ACTIVE;
        }
        return c;
    }

    private static int countNeighbours(char[] cubes, int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW)
    {
        char[] n = new char[80];
        int offset = 0;
        for (int oX = -1; oX < 2; oX++)
        {
            for (int oY = -1; oY < 2; oY++)
            {
                for (int oZ = -1; oZ < 2; oZ++)
                {
                    for (int oW = -1; oW < 2; oW++)
                    {
                        if (0 != oX || 0 != oY || 0 != oZ || 0 != oW)
                        {
                            n[offset++] = getCube(cubes, x + oX, y + oY, z + oZ, w + oW, totalX, totalY, totalZ, totalW);
                        }
                    }
                }
            }
        }
        return countCubes(n);
    }

    private static int countCubes(char[] cubes)
    {
        int count = 0;
        for (char cube : cubes)
        {
            if (cube == ACTIVE)
            {
                count++;
            }
        }
        return count;
    }

    private static void parseLine(char[] cubes, String line, int startX, int startY, int startZ, int startW, int totalX, int totalY, int totalZ, int totalW)
    {
        for (int i = 0; i < line.length(); i++)
        {
            setCube(cubes, startX + i, startY, startZ, startW, totalX, totalY, totalZ, totalW, line.charAt(i));
        }
    }

    private static void setCube(char[] cubes, int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW, char cube)
    {
        if (isValid(x, y, z, w, totalX, totalY, totalZ, totalW))
        {
            cubes[getAddress(x, y, z, w, totalX, totalY, totalZ, totalW)] = cube;
        }
    }

    private static char getCube(char[] cubes, int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW)
    {
        if (!isValid(x, y, z, w, totalX, totalY, totalZ, totalW))
        {
            return INACTIVE;
        }
        return cubes[getAddress(x, y, z, w, totalX, totalY, totalZ, totalW)];
    }

    private static int getAddress(int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW)
    {
        return w * totalZ * totalY * totalX + z * totalX * totalY + y * totalX + x;
    }

    private static boolean isValid(int x, int y, int z, int w, int totalX, int totalY, int totalZ, int totalW)
    {
        return
                x >= 0 && x < totalX &&
                        y >= 0 && y < totalY &&
                        z >= 0 && z < totalZ &&
                        w >= 0 && w < totalW;
    }
}
