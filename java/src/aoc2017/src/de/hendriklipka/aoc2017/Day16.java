package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day16 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day16().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> config=data.getLines();
        int count=Integer.parseInt(config.get(0));
        List<Character> prgs=new ArrayList<>();
        for (int i=0;i<count;i++)
        {
            prgs.add((char)('a'+i));
        }
        String[] moves= StringUtils.split(config.get(1), ",");
        for (String move : moves)
        {
            prgs=doMove(prgs, move);
        }
        return StringUtils.join(prgs,"");
    }

    private List<Character> doMove(final List<Character> prgs, final String move)
    {
        List<Character> result=null;
        if (move.charAt(0)=='s')
        {
            result=new ArrayList<>();
            int len=prgs.size();
            int count= AocParseUtils.parseIntFromString(move, "s(\\d+)");
            result.addAll(prgs.subList(len-count, len));
            result.addAll(prgs.subList(0, len-count));
        }
        if (move.charAt(0)=='x')
        {
            result=prgs;
            int from = AocParseUtils.parseIntFromString(move, "x(\\d+)/\\d+");
            int to = AocParseUtils.parseIntFromString(move, "x\\d+/(\\d+)");
            char h=result.get(from);
            result.set(from,result.get(to));
            result.set(to,h);
        }
        if (move.charAt(0)=='p')
        {
            result = prgs;
            String from = AocParseUtils.parseStringFromString(move, "p(.)/.");
            String to = AocParseUtils.parseStringFromString(move, "p./(.)");
            int fromPos=result.indexOf(from.charAt(0));
            int toPos=result.indexOf(to.charAt(0));
            result.set(fromPos,to.charAt(0));
            result.set(toPos,from.charAt(0));
        }

        return result;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> config = data.getLines();
        int count = Integer.parseInt(config.get(0));
        List<Character> prgs = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            prgs.add((char) ('a' + i));
        }
        String[] moves = StringUtils.split(config.get(1), ",");
        int firstMatch=-1;
        final var dances = 1000000000;
        for (int d = 0; d < dances; d++)
        {
            for (String move : moves)
            {
                prgs = doMove(prgs, move);
            }
            // check for a round in which we see the starting configuration again
            boolean allMatch=true;
            for (int i=0;i<prgs.size();i++)
            {
                if (prgs.get(i)!=(char)('a'+i))
                {
                    allMatch=false;
                    break;
                }
            }
            if (allMatch)
            {
                // when this happens for the second time, we know the period of the dance moves
                if (firstMatch==-1)
                {
                    firstMatch=d;
                }
                else
                {
                    // so we can jump ahead and only to the last rounds
                    int period=d-firstMatch;
                    System.out.println("period="+period+", starting at "+d);
                    while (d+period<dances)
                    {
                        d+=period;
                    }
                    System.out.println("jumped ahead to "+d);
                }
            }
        }
        return StringUtils.join(prgs, "");
    }
}
