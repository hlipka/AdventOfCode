package de.hendriklipka.aoc2020.day17;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day171
{
    private static final char INACTIVE ='.';
    private static final char ACTIVE ='#';

    private static final int ITERATIONS=6;

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("../data/2020/day17.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            int rows=lines.size();
            int cols=lines.get(0).length();
            int totalY = rows + 2*ITERATIONS;
            int totalX = cols + 2*ITERATIONS;
            int totalZ = 1+2*ITERATIONS;
            char[] cubes=new char[totalY * totalX * totalZ];
            Arrays.fill(cubes, '.');
            int currentZ=ITERATIONS;
            int currentY=ITERATIONS;
            for (String line: lines)
            {
                parseLine(cubes, line, ITERATIONS, currentY++, currentZ, totalX, totalY, totalZ);
            }
            dumpCubes(cubes, totalX, totalY, totalZ);

            for (int iteration=0; iteration<ITERATIONS; iteration++)
            {
                cubes=updateCubes(cubes, totalX, totalY, totalZ);
                dumpCubes(cubes, totalX, totalY, totalZ);
            }
            System.out.println(countCubes(cubes));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static char[] updateCubes(char[] cubes, int totalX, int totalY, int totalZ)
    {
        char[] newCubes = new char[cubes.length];
        for (int z=0;z<totalZ;z++)
        {
            for (int x=0;x<totalX;x++)
            {
                for (int  y=0;y<totalY;y++)
                {
                    newCubes[getAddress(x,y,z,totalX, totalY, totalZ)]=getNewCube(cubes, x,y,z,totalX, totalY, totalZ);
                }
            }
        }
        return newCubes;
    }

    private static char getNewCube(char[] cubes, int x, int y, int z, int totalX, int totalY, int totalZ)
    {
        char c= cubes[getAddress(x,y,z,totalX, totalY, totalZ)];
        int n=countNeighbours(cubes, x,y,z,totalX, totalY, totalZ);
        if (c== ACTIVE && (n<2 || n>3))
            return INACTIVE;
        if (c== INACTIVE && n==3)
            return ACTIVE;
        return c;
    }

    private static int countNeighbours(char[] cubes, int x, int y, int z, int totalX, int totalY, int totalZ)
    {
        char[] n=new char[26];
        n[0]=getCube(cubes, x-1,y-1,z-1,totalX, totalY, totalZ);
        n[1]=getCube(cubes, x-1,y,z-1,totalX, totalY, totalZ);
        n[2]=getCube(cubes, x-1,y+1,z-1,totalX, totalY, totalZ);

        n[3]=getCube(cubes, x,y-1,z-1,totalX, totalY, totalZ);
        n[4]=getCube(cubes, x,y,z-1,totalX, totalY, totalZ);
        n[5]=getCube(cubes, x,y+1,z-1,totalX, totalY, totalZ);

        n[6]=getCube(cubes, x+1,y-1,z-1,totalX, totalY, totalZ);
        n[7]=getCube(cubes, x+1,y,z-1,totalX, totalY, totalZ);
        n[8]=getCube(cubes, x+1,y+1,z-1,totalX, totalY, totalZ);


        n[9]=getCube(cubes, x-1,y-1,z,totalX, totalY, totalZ);
        n[10]=getCube(cubes, x-1,y,z,totalX, totalY, totalZ);
        n[11]=getCube(cubes, x-1,y+1,z,totalX, totalY, totalZ);

        n[12]=getCube(cubes, x,y-1,z,totalX, totalY, totalZ);
        n[13]=getCube(cubes, x,y+1,z,totalX, totalY, totalZ);

        n[14]=getCube(cubes, x+1,y-1,z,totalX, totalY, totalZ);
        n[15]=getCube(cubes, x+1,y,z,totalX, totalY, totalZ);
        n[16]=getCube(cubes, x+1,y+1,z,totalX, totalY, totalZ);

        n[17]=getCube(cubes, x-1,y-1,z+1,totalX, totalY, totalZ);
        n[18]=getCube(cubes, x-1,y,z+1,totalX, totalY, totalZ);
        n[19]=getCube(cubes, x-1,y+1,z+1,totalX, totalY, totalZ);

        n[20]=getCube(cubes, x,y-1,z+1,totalX, totalY, totalZ);
        n[21]=getCube(cubes, x,y,z+1,totalX, totalY, totalZ);
        n[22]=getCube(cubes, x,y+1,z+1,totalX, totalY, totalZ);

        n[23]=getCube(cubes, x+1,y-1,z+1,totalX, totalY, totalZ);
        n[24]=getCube(cubes, x+1,y,z+1,totalX, totalY, totalZ);
        n[25]=getCube(cubes, x+1,y+1,z+1,totalX, totalY, totalZ);
        return countCubes(n);
    }

    private static int countCubes(char[] cubes)
    {
        int count=0;
        for (int i=0;i<cubes.length;i++)
        {
            if (cubes[i]== ACTIVE)
                count++;
        }
        return count;
    }

    private static void dumpCubes(char[] cubes, int totalX, int totalY, int totalZ)
    {
        for (int z=0;z<totalZ;z++)
        {
            if (!planeIsEmpty(cubes, z, totalX, totalY, totalZ))
            {
                dumpPlane(cubes, z, totalX, totalY, totalZ);
            }
        }
    }

    private static boolean planeIsEmpty(char[] cubes, int z, int totalX, int totalY, int totalZ)
    {
        for (int x=0;x<totalX;x++)
        {
            for (int  y=0;y<totalY;y++)
            {
                if (getCube(cubes, x, y, z, totalX, totalY, totalZ)== ACTIVE)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static void dumpPlane(char[] cubes, int z, int totalX, int totalY, int totalZ)
    {
        System.out.println("z="+z);
        for (int x=0;x<totalX;x++)
        {
            for (int  y=0;y<totalY;y++)
            {
                char c=getCube(cubes, x, y, z, totalX, totalY, totalZ);
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void parseLine(char[] cubes, String line, int startX, int startY, int startZ, int totalX, int totalY, int totalZ)
    {
        for (int i=0;i<line.length();i++)
        {
            setCube(cubes, startX+i, startY, startZ, totalX, totalY, totalZ, line.charAt(i));
        }
    }

    private static void setCube(char[] cubes, int x, int y, int z, int totalX, int totalY, int totalZ, char cube)
    {
        if (isValid(x,y,z,totalX, totalY, totalZ))
        cubes[getAddress(x,y,z,totalX, totalY, totalZ)]=cube;
    }

    private static char getCube(char[] cubes, int x, int y, int z, int totalX, int totalY, int totalZ)
    {
        if (!isValid(x,y,z,totalX, totalY, totalZ))
            return INACTIVE;
        return cubes[getAddress(x,y,z,totalX, totalY, totalZ)];
    }

    private static int getAddress(int x, int y, int z, int totalX, int totalY, int totalZ)
    {
        return z*totalX*totalY+y*totalX+x;
    }

    private static boolean isValid(int x, int y, int z, int totalX, int totalY, int totalZ)
    {
        return
                x>=0&&x<totalX &&
                y>=0&&y<totalY &&
                z>=0&&z<totalZ;
    }
}
