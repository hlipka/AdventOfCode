package de.hendriklipka.aoc2016.day14;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 23:18
 */
public class Day14b
{
//    final static String startHash = "abc";
    final static String startHash = "zpqevtbw";
    final static long limit = 1000;
    final static long keyCount = 64;


    public static void main(String[] args)
    {
        long index=0;
        int foundKeys=0;
        LinkedList<String> hashes = new LinkedList<>();
        for (long i = 0; i< limit+1; i++)
        {
            hashes.add(getHash(i));
        }
        while (foundKeys< keyCount)
        {
            String currentHash=hashes.getFirst();
            hashes.removeFirst();
            if (isKey(currentHash, hashes))
            {
                foundKeys++;
            }
            hashes.add(getHash(index+ limit+1L));
            index++;
        }
        // 16119 is too high
        System.out.println(index-1);
    }

    private static boolean isKey(final String currentHash, final LinkedList<String> hashes)
    {
        String searchFor = getSearchFor(currentHash);
        if (null!=searchFor)
        {
            for (String hash: hashes)
            {
                if (hash.contains(searchFor))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getSearchFor(final String hash)
    {
        for (int i=0;i<30;i++)
        {
            if (hash.charAt(i)== hash.charAt(i+1) && hash.charAt(i) ==hash.charAt(i+2))
            {
                return StringUtils.repeat(hash.charAt(i), 5);
            }
        }
        return null;
    }

    private static String getHash(final long index)
    {
        String hash = DigestUtils.md5Hex(startHash + index).toLowerCase();
        for (int i=0;i<2016;i++)
        {
            hash = DigestUtils.md5Hex(hash);
        }
        return hash;
    }
}
