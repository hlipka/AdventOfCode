package de.hendriklipka.aoc2020.day23;

/**
 * User: hli
 * Date: 23.12.20
 * Time: 11:46
 */
public class Day231
{

    public static void main(String[] args)
    {
        playGame();
    }

    private static void playGame()
    {
        Cups cups=parseGame();
        dumpState(cups);
        for (int round = 0; round< 100; round++)
        {
            playRound(cups);
        }
        Cup one=cups.findOne();
        dumpFullChain(one.next, one);
    }

    private static void playRound(final Cups cups)
    {
        Cup taken=cups.remove();
        dumpChain(taken);
        cups.selectDestination();
        cups.insert(taken);
        cups.selectNewCurrent();
        dumpState(cups);
    }

    private static void dumpState(final Cups cups)
    {
        final Cup stop = cups.current;
        dumpFullChain(stop, stop);
    }

    private static void dumpFullChain(Cup out, final Cup stop)
    {
        System.out.print("("+ out.num+")");
        out = out.next;
        while (out != stop)
        {
            System.out.print(" "+ out.num);
            out = out.next;
        }
        System.out.println();
    }

    private static void dumpChain(Cup taken)
    {
        System.out.print("pick up");
        while (null!=taken)
        {
            System.out.print(" "+taken.num);
            taken=taken.next;
        }
        System.out.println();
    }

    private static Cups parseGame()
    {
        byte[] b= "496138527".getBytes();
        Cup last=null;
        Cup first=null;
        for (final byte value : b)
        {
            Cup next = new Cup((char) value, last);
            if (null == first)
            {
                first = next;
            }
            last = next;
        }
        first.previous=last;
        last.next=first;
        Cups game=new Cups();
        game.current=first;
        return game;
    }

    private static class Cups
    {
        private static final int MAX_NUM = 9;
        public Cup current;
        public Cup destination;

        public Cup remove()
        {

            final Cup taken=current.next;
            taken.previous=null;
            current.next=skip(current);
            current.next.previous.next=null; // set the last element of the taken chain as null
            current.next.previous=current;
            return taken;
        }

        private Cup skip(Cup current)
        {
            for (int i = 0; i< 3; i++)
            {
                current=current.next;
            }
            return current.next;
        }

        public void selectDestination()
        {
            destination=null;
            int nextNum=current.num-1;
            Cup lookingAt=current;
            while (destination==null)
            {
                if (lookingAt.num==nextNum)
                {
                    destination=lookingAt;
                }
                else
                {
                    lookingAt = lookingAt.next;
                    if (lookingAt == current)
                    {
                        if (0 == nextNum)
                        {
                            nextNum = MAX_NUM;
                        }
                        else
                        {
                            nextNum -= 1;
                        }
                        if (nextNum == current.num)
                        {
                            throw new IllegalStateException("could not determine target cup");
                        }
                    }
                }
            }
        }

        public void insert(final Cup taken)
        {
            Cup seam=destination.next;
            destination.next=taken;
            taken.previous=destination;
            Cup last=getLastCup(taken);
            last.next=seam;
            seam.previous=last;
        }

        private Cup getLastCup(Cup taken)
        {
            while (taken.next!=null)
                taken=taken.next;
            return taken;
        }

        public void selectNewCurrent()
        {
            current=current.next;
        }

        public Cup findOne()
        {
            Cup lookingAt=current;
            while (lookingAt.num!=1)
            {
                lookingAt=lookingAt.next;
            }
            return lookingAt;
        }
    }

    private static class Cup
    {
        public Cup next;
        public Cup previous;
        public int num;

        public Cup(final char cup, final Cup last)
        {
            previous=last;
            if (null!=previous)
            {
                previous.next=this;
            }
            num=cup-'0';
        }
    }
}
