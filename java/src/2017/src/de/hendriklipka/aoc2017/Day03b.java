package de.hendriklipka.aoc2017;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * User: hli
 */
public class Day03b
{
    public static void main(String[] args)
    {
        int value = 368078;
        Map<Pair<Integer,Integer>, Integer> memory = new HashMap<>();
        memory.put(Pair.of(0,0), 1 ); // start
        memory.put(Pair.of(1,0), 1 ); // to the right (x is positive)
        memory.put(Pair.of(1,1), 2 ); // one up (y is positive)
        int x=1;
        int y=1;
        int dir=1; // left (0=up, 1=left, 2=down, 3=right)

        while (true)
        {
            int cell=-1;
            switch(dir)
            {
                case 0:
                    y+=1;
                    if (2==countCells(memory, x,y))
                        dir=1;
                    break;
                case 1:
                    x-=1;
                    if (2 == countCells(memory, x, y))
                        dir = 2;
                    break;
                case 2:
                    y-=1;
                    if (2 == countCells(memory, x, y))
                        dir = 3;
                    break;
                case 3:
                    x+=1;
                    if (2 == countCells(memory, x, y))
                        dir = 0;
                    break;
            }
            cell = sumCells(memory, x, y);
            if (cell>value)
            {
                System.out.println(cell);
                break;
            }
            memory.put(Pair.of(x, y), cell);
        }
    }

    private static int countCells(final Map<Pair<Integer, Integer>, Integer> memory, final int x, final int y)
    {
        int count=0;
        if (-1!=getCell(memory, x-1, y, -1)) count++;
        if (-1!=getCell(memory, x-1, y+1, -1)) count++;
        if (-1!=getCell(memory, x-1, y-1, -1)) count++;

        if (-1!=getCell(memory, x+1, y, -1)) count++;
        if (-1!=getCell(memory, x+1, y+1, -1)) count++;
        if (-1!=getCell(memory, x+1, y-1, -1)) count++;

        if (-1!=getCell(memory, x, y-1, -1)) count++;
        if (-1!=getCell(memory, x, y+1, -1)) count++;
        return count;
    }

    private static int sumCells(final Map<Pair<Integer, Integer>, Integer> memory, final int x, final int y)
    {
        int sum = 0;
        sum += getCell(memory, x - 1, y, 0);
        sum += getCell(memory, x - 1, y + 1, 0);
        sum += getCell(memory, x - 1, y - 1, 0);

        sum += getCell(memory, x + 1, y, 0);
        sum += getCell(memory, x + 1, y + 1, 0);
        sum += getCell(memory, x + 1, y - 1, 0);

        sum += getCell(memory, x, y - 1, 0);
        sum += getCell(memory, x, y + 1, 0);
        return sum;
    }

    
    private static int getCell(final Map<Pair<Integer, Integer>, Integer> memory, final int x, final int y, final int defaultValue)
    {
        return memory.getOrDefault(Pair.of(x,y), defaultValue);
    }
}
