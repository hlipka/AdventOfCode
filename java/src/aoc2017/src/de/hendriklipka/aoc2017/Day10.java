package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<Integer>> config=data.getLineIntegers(",");
        int size=config.get(0).get(0);
        List<Integer> lengths=config.get(1);
        KnotHash hash=new KnotHash(size, lengths);
        hash.hash();
        return hash.getHash();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return null;
        List<String> config = data.getLines();
        int size=Integer.parseInt(config.get(0));

        System.out.println(calculateHash("", size));
        System.out.println(calculateHash("AoC 2017", size));
        System.out.println(calculateHash("1,2,3", size));
        System.out.println(calculateHash("1,2,4", size));
        return calculateHash(config.get(1), size);
    }

    private String calculateHash(String s, final int size)
    {
        List<Integer> lengths = new ArrayList<>();
        final var bytes = s.toCharArray();
        for (char c: bytes)
        {
            lengths.add((int)c);
        }
        lengths.add(17);
        lengths.add(31);
        lengths.add(73);
        lengths.add(47);
        lengths.add(23);
        KnotHash hash = new KnotHash(size, lengths);

        for (int r=0;r<64;r++)
        {
            hash.hash();
        }
        return hash.getHexHash();
    }

    private static class KnotHash
    {
        int pos=0;
        int skip=0;
        final int size;
        int[] data;
        private final List<Integer> lengths;

        public KnotHash(final int size, final List<Integer> lengths)
        {
            this.size = size;
            data = new int[size];
            this.lengths = lengths;
            for (int i = 0; i < size; i++)
            {
                data[i] = i;
            }
        }

        public void hash()
        {
            for (int length : lengths)
            {
                for (int i = 0; i < length / 2; i++)
                {
                    int from = (pos + i) % size;
                    int to = (pos + length - i - 1) % size;
                    int h = data[from];
                    data[from] = data[to];
                    data[to] = h;
                }
                pos = (pos + length + skip) % size;
                skip++;
            }

        }

        public int getHash()
        {
            return data[0]*data[1];
        }

        public String getHexHash()
        {
            char[] reducedHash=new char[16];
            for (int i=0;i<16;i++)
            {
                int r=0;
                for (int j=0;j<16;j++)
                {
                    r = r^data[i*16+j];
                }
                reducedHash[i]=(char)r;
            }
            StringBuilder result= new StringBuilder();
            for (char c: reducedHash)
            {
                var hexString = Integer.toHexString((int) c & 0xff);
                // make sure we get a 2-char hex value
                if (hexString.length() == 1)
                    hexString="0"+hexString;
                result.append(hexString);
            }
            return result.toString();
        }
    }
}
