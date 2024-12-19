package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;

public class Day19 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day19().doPuzzle(args);
    }

    @Override
    protected Object solvePartA()
    {
        // when we simulate the first we see that:
        // the number of the winning elf increases by 2 with each new elf in the round
        // once it wraps around elf 1 wins the next round
        // we instead of doing the stealing, we just count the number up
        int idx=3;
        int elf=3;
        while (idx< 3018458)
        {
            idx++;
            elf+=2;
            elf=elf%(idx+1);
            if (0==elf)
                elf=1;
        }
        System.out.println(idx + "->" + elf);
        return null;
    }

    @Override
    protected Object solvePartB()
    {
        // this is a simulation code to make some observations
        for (int i=3;i<1000;i++)
        {
            System.out.println(i+"->"+simulate(i));
        }

        /*
            What we find is:
            - there is a pattern when the first elf gets the presents
            - we start with idx=2 and diff=2
            - then we do idx+=diff and diff *=3
            - this gives us 4, 10, 28, 82, 244, 730 and so on
            - starting with idx, the number of the elf with the presents increases by 1
            - up until idx=2*elf
            - and from there on idx increases by two (until idx==elf, where the cycle start again)
            So we can use that to just calculate the number we need
         */
        final int target= 3018458;
        int idx=2;
        int diff=2;
        while (idx+diff< target)
        {
            idx+=diff;
            diff*=3;
        }
        int elf=1;
        while (idx < target)
        {
            if (2*elf<idx)
            {
                idx++;
                elf++;
            }
            else
            {
                idx++;
                elf+=2;
            }
        }
        // doing the actual simulation takes around 3.5h (depending on the CPU)
        // System.out.println(simulate(3018458));
        return elf;
    }

    private int simulate(final int count)
    {
        Elf first = new Elf();
        first.num=1;
        Elf last=first;
        for (int i=1;i<count;i++)
        {
            Elf next=new Elf();
            next.num=i+1;
            last.left=next;
            next.right=last;
            last=next;
        }
        first.right=last;
        last.left=first;
        Elf current=first;
        while (current.left!=current.right)
        {
            removeOpposite(current);
            current = current.left;
        }
        return current.num;
    }

    private void removeOpposite(final Elf current)
    {
        // we find the elf from which we steal by starting at the current elf, and moving to the right and to the left at the same time
        // either we end up at the same elf (which is the at the direct opposite, so we remove it directly)
        // or with two neighbours (so we remove the left one of these)
        // this is rather slow for the full set, but easy to implement, and it saves the math of finding the opposite elf
        // (for large data using an array / ArrayList might be faster then
        Elf currentLeft=current.left;
        Elf currentRight=current.right;
        while (currentLeft!=currentRight && currentLeft.left!=currentRight)
        {
            currentLeft=currentLeft.left;
            currentRight=currentRight.right;
        }
        if (currentLeft==currentRight) // direct opposite
        {
            // move both pointers one step back, and remove the elf in the middle
            currentLeft=currentLeft.right;
            currentRight=currentRight.left;
            currentLeft.left=currentRight;
            currentRight.right=currentLeft;
        }
        else
        {
            currentLeft = currentLeft.right;
            currentLeft.left = currentRight;
            currentRight.right = currentLeft;

        }

    }

    private static class Elf
    {
        int num;
        Elf left;
        Elf right;

        @Override
        public String toString()
        {
            return "Elf{" +
                   "num=" + num +
                   '}';
        }
    }
}
