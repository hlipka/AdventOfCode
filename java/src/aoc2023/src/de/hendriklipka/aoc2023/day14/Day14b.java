package de.hendriklipka.aoc2023.day14;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day14b
{

    public static final char ROUND_ROCK = 'O';
    public static final char FREE = '.';

    public static void main(String[] args)
    {
        try
        {
            int total=1000000000;
            List<List<List<Character>>> test=new ArrayList<>();
            List<List<Character>> field = AocParseUtils.getLinesAsChars("2023", "ex14");
            test.add(copyField(field));
            int size = field.size();
            int cycleStart=-1;
            int cycleEnd=-1;
            for (int i=0;i<1000;i++)
            {
                System.out.println(i);
                doCycle(field);

                int recent = findRecent(test, field);
                if (-1 != recent)
                {
                   cycleStart=recent;
                   cycleEnd=i;
                   break;
                }
                test.add(copyField(field));
            }
            System.out.println("found cycle at "+cycleEnd+" to "+cycleStart);
            int cycleLength=cycleEnd-cycleStart+1;
            System.out.println("iterate with "+cycleLength+" hops, next is "+(cycleEnd+cycleLength));
            while(cycleStart+cycleLength<total-1)
            {
                cycleStart+=cycleLength;
            }
            System.out.println("ending up at cycle "+cycleStart);
            if (cycleStart<total-1)
            {
                for(int i=cycleStart;i<total;i++)
                {
                    doCycle(field);
                }
            }
            dumpField(field);
            long sum=0;
            for (int i = 0; i < size; i++)
            {
                int weight = size - i;
                long lineWeight = weight * field.get(i).stream().filter(c -> c == ROUND_ROCK).count();
                sum+= lineWeight;
            }
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int findRecent(List<List<List<Character>>> test, List<List<Character>> field)
    {
        for (int i=0;i<test.size();i++)
            if (isSame(test.get(i), field))
                return i;
        return -1;
    }

    private static boolean isSame(List<List<Character>> field, List<List<Character>> lastField)
    {
        for (int i=0;i<field.size();i++)
        {
            if (!isSameLine(field.get(i), lastField.get(i)))
                return false;
        }
        return true;
    }

    private static boolean isSameLine(List<Character> line1, List<Character> line2)
    {
        for (int i=0;i<line1.size();i++)
        {
            if (line1.get(i)!=line2.get(i))
                return false;
        }
        return true;
    }

    private static List<List<Character>> copyField(List<List<Character>> field)
    {
        List<List<Character>> newField=new ArrayList<>();
        for (List<Character> line: field)
        {
            newField.add(new ArrayList<>(line));
        }
        return newField;
    }

    private static void doCycle(List<List<Character>> field)
    {
        int size=field.size();
        moveAllNorth(size, field);
        moveAllWest(field);
        moveAllSouth(size, field);
        moveAllEast(size, field);
    }

    private static void moveAllNorth(int size, List<List<Character>> field)
    {
        for (int i = 1; i < size; i++)
        {
            List<Character> line = field.get(i);
            moveNorth(line, i, field);
        }
    }

    private static void moveAllSouth(int size, List<List<Character>> field)
    {
        for (int i = size-1; i >= 0; i--)
        {
            List<Character> line = field.get(i);
            moveSouth(line, i, field);
        }
    }

    private static void moveAllWest(List<List<Character>> field)
    {
        for (List<Character> line : field)
        {
            moveWest(line);
        }
    }

    private static void moveWest(List<Character> line)
    {
        for (int i=1;i<line.size(); i++)
        {
            if (line.get(i)==ROUND_ROCK)
            {
                int col = i;
                while (col > 0 && line.get(col - 1) == FREE)
                    col--;
                line.set(i, FREE);
                line.set(col, ROUND_ROCK);
            }
        }
    }
    private static void moveEast(List<Character> line)
    {
        for (int i=line.size()-1;i>=0; i--)
        {
            if (line.get(i)==ROUND_ROCK)
            {
                int col = i;
                while (col < line.size() - 1 && line.get(col + 1) == FREE)
                    col++;
                line.set(i, FREE);
                line.set(col, ROUND_ROCK);
            }
        }
    }

    private static void moveAllEast(int size, List<List<Character>> field)
    {
        for (int i = 0; i < size; i++)
        {
            List<Character> line = field.get(i);
            moveEast(line);
        }
    }

    private static void dumpField(List<List<Character>> field)
    {
        for (List<Character> line: field)
        {
            System.out.println(StringUtils.join(line,""));
        }
        System.out.println("----");
    }

    private static void moveNorth(List<Character> line, int row, List<List<Character>> field)
    {
        int origRow= row;
        for (int i=0;i<line.size();i++)
        {
            if (line.get(i) == ROUND_ROCK)
            {
                row=origRow;
                while (row > 0 && field.get(row - 1).get(i) == FREE)
                {
                    row--;
                }
                field.get(origRow).set(i, FREE);
                field.get(row).set(i, ROUND_ROCK);
            }
        }
    }

    private static void moveSouth(List<Character> line, int row, List<List<Character>> field)
    {
        int origRow= row;
        for (int i=0;i<line.size();i++)
        {
            if (line.get(i) == ROUND_ROCK)
            {
                row=origRow;
                while (row < field.size() - 1 && field.get(row + 1).get(i) == FREE)
                {
                    row++;
                }
                field.get(origRow).set(i, FREE);
                field.get(row).set(i, ROUND_ROCK);
            }
        }
    }

}
