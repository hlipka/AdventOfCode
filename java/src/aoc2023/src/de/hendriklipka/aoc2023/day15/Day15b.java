package de.hendriklipka.aoc2023.day15;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hli
 * Date: 14.12.23
 * Time: 19:13
 */
public class Day15b
{
    static Box[] boxes = new Box[256];
    public static void main(String[] args)
    {
        try
        {
            String testLine=AocParseUtils.getLines("2023", "ex15").get(0);
            String line=AocParseUtils.getLines("2023", "day15").get(0);
            System.out.println(simulate(testLine));
            System.out.println(simulate(line));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long simulate(String line)
    {
        for (int i=0;i<256;i++)
            boxes[i]=new Box();
        String[] parts = line.split(",");
        for (String s : parts)
            execute(s);
        long sum=0;
        for (int i=0;i<256;i++)
        {
            sum+=boxes[i].power()*(i+1);
        }
        return sum;
    }

    private static void execute(String s)
    {
        if (s.endsWith("-"))
        {
            final String label = s.substring(0, s.length() - 1);
            final int hashValue = hashValue(label);
            Box box = boxes[hashValue];
            box.remove(label);
        }
        else
        {
            int pos= s.indexOf('=');
            final String label = s.substring(0, pos);
            final int hashValue = hashValue(label);
            Box box = boxes[hashValue];
            box.add(label, s.substring(pos + 1));
        }
    }

    private static int hashValue(String s)
    {
        int h = 0;
        for(char c: s.toCharArray())
        {
            h+= c;
            h*=17;
            h=h%256;
        }
        return h;
    }

    private static class Box
    {
        List<Lens> lenses=new LinkedList<>();

        private void add(String lens, String power)
        {
            Lens l=null;
            for (Lens l1: lenses)
            {
                if (l1.label.equals(lens))
                {
                    l=l1;
                    break;
                }
            }
            if (null!=l)
                l.power=power;
            else
                lenses.add(new Lens(lens, power));
        }

        private void remove(String lens)
        {
            for (Lens l1 : lenses)
            {
                if (l1.label.equals(lens))
                {
                    lenses.remove(l1);
                    break;
                }
            }
        }

        public long power()
        {
            long sum=0;
            for (int i=0;i<lenses.size();i++)
                sum+=(i+1)*Long.parseLong(lenses.get(i).power);
            return sum;
        }
    }

    private static class Lens
    {
        final String label;
        String power;

        public Lens(String lens, String power)
        {
            this.label=lens;
            this.power=power;
        }
    }
}
