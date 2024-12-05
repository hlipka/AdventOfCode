package de.hendriklipka.aoc2023.day18;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Day18a1
{
    static Position currentPos=new Position(0,0);
    static Map<Position, String> field = new HashMap<>();
    static int upMost=0;
    static int downMost=0;
    static int leftMost=0;
    static int rightMost=0;
    public static void main(String[] args)
    {
        try
        {
            field.put(currentPos, "S");
            AocDataFileUtils.getLines("2023", "day18").forEach(Day18a1::dig);
            fillField();
            dumpField();
            System.out.println(field.size());
            // 31722 is too low
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void dumpField()
    {
        for (int row=upMost;row<=downMost;row++)
        {
            for (int col=leftMost;col<=rightMost;col++)
            {
                Position pos = new Position(row, col);
                System.out.print(field.getOrDefault(pos, "."));
            }
            System.out.println();
        }
    }

    // this uses an odd-even algorithm to fill the field
    private static void fillField()
    {
        for (int row=upMost;row<=downMost;row++)
        {
            boolean inside=false;
            boolean wall=false;
            boolean downWall=false;
            int wallLength=0;
            for (int col=leftMost;col<=rightMost;col++)
            {
                Position pos = new Position(row, col);
                if (field.containsKey(pos))
                {
                    if (!wall)
                    {
                        wall=true;
                        downWall=field.containsKey(pos.updated(Direction.DOWN));
                        wallLength=1;
                    }
                    else {
                        wallLength++;
                    }
                }
                else
                {
                    if (wall)
                    {
                        wall=false;
                        if (1==wallLength)
                            inside=!inside;
                        else
                        {
                            if (downWall)
                            {
                                if (!field.containsKey(pos.updated(Direction.LEFT).updated(Direction.DOWN)))
                                    inside = !inside;
                            }
                            else
                                if (field.containsKey(pos.updated(Direction.LEFT).updated(Direction.DOWN)))
                                    inside = !inside;
                        }
                    }
                    if (inside)
                        field.put(pos, "+");
                }
            }
        }
    }

    private static void dig(String line)
    {
        String dirStr=AocParseUtils.parseStringFromString(line,"([RLUD]*) .*");
        int length=AocParseUtils.parseIntFromString(line, ".* (\\d+) .*");
        for (int i=0;i<length;i++)
        {
            Direction dir=switch(dirStr.charAt(0))
            {
                case 'R'->Direction.RIGHT;
                case 'L'->Direction.LEFT;
                case 'U'->Direction.UP;
                case 'D'->Direction.DOWN;
                default ->throw new IllegalArgumentException(dirStr);
            };
            currentPos = currentPos.updated(dir);
            field.put(currentPos, "#");
            leftMost=Math.min(leftMost, currentPos.col);
            rightMost=Math.max(rightMost, currentPos.col);
            upMost=Math.min(upMost,currentPos.row);
            downMost=Math.max(downMost, currentPos.row);
        }
    }
}
