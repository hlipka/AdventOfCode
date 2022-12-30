package de.hendriklipka.aoc2020.day25;

/**
 * User: hli
 * Date: 25.12.20
 * Time: 12:09
 */
public class Day251
{
    public static void main(String[] args)
    {
        // test values
//        final long cardPublicKey = 5764801L;
//        final long doorPublicKey = 17807724L;

        // real input
        final long cardPublicKey = 3248366L;
        final long doorPublicKey = 4738476;

        long roundsCard= determineRounds(cardPublicKey);
        long roundsDoor=determineRounds(doorPublicKey);

        System.out.println(roundsCard);
        System.out.println(roundsDoor);
        long enc1=transformWithLoopSize(cardPublicKey, roundsDoor);
        long enc2= transformWithLoopSize(doorPublicKey, roundsCard);

        System.out.println(enc1);
        System.out.println(enc2);
    }

    private static long determineRounds(final long key)
    {
        int rounds=0;
        long r=1L;
        while (r!=key)
        {
            r=transform(r, 7L);
            rounds++;
        }
        return rounds;
    }

    private static long transformWithLoopSize(final long key, final long rounds)
    {
        long r=1L;
        for (int i=0;i<rounds;i++)
        {
            r=transform(r, key);
        }
        return r;
    }

    private static long transform(final long value, final long subject)
    {
        return value*subject % 20201227;
    }
}
