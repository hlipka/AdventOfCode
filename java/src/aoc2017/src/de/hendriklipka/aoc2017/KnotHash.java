package de.hendriklipka.aoc2017;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 */
class KnotHash
{
    int pos = 0;
    int skip = 0;
    final int size;
    int[] data;
    private final List<Integer> lengths;
    char[] reducedHash = new char[16];

    public KnotHash(String s, final int size)
    {
        lengths = new ArrayList<>();
        final var bytes = s.toCharArray();
        for (char c : bytes)
        {
            lengths.add((int) c);
        }
        lengths.add(17);
        lengths.add(31);
        lengths.add(73);
        lengths.add(47);
        lengths.add(23);

        this.size = size;
        data = new int[size];
        for (int i = 0; i < size; i++)
        {
            data[i] = i;
        }
    }

    public KnotHash(final List<Integer> lengths, final int size)
    {
        this.size = size;
        this.lengths = lengths;
        data = new int[size];
        for (int i = 0; i < size; i++)
        {
            data[i] = i;
        }
    }

    public void hash()
    {
        for (int r = 0; r < 64; r++)
        {
            singleRound();
        }
        // reduce the hash
        for (int i = 0; i < 16; i++)
        {
            int r = 0;
            for (int j = 0; j < 16; j++)
            {
                r = r ^ data[i * 16 + j];
            }
            reducedHash[i] = (char) r;
        }
    }

    void singleRound()
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
        return data[0] * data[1];
    }

    public String getHexHash()
    {
        StringBuilder result = new StringBuilder();
        for (char c : reducedHash)
        {
            var hexString = Integer.toHexString((int) c & 0xff);
            // make sure we get a 2-char hex value
            if (hexString.length() == 1)
                hexString = "0" + hexString;
            result.append(hexString);
        }
        return result.toString();
    }

    public String getBinaryHash()
    {
        StringBuilder result = new StringBuilder();
        for (char c : reducedHash)
        {
            var binString = Integer.toBinaryString((int) c & 0xff);
            // make sure we get all bits
            binString=StringUtils.leftPad(binString, 8, '0');
            result.append(binString);
        }
        return result.toString();
    }
}
