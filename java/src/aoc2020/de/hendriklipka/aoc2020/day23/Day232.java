package de.hendriklipka.aoc2020.day23;

/**
 * User: hli
 * Date: 23.12.20
 * Time: 11:46
 */
public class Day232
{
    public static void main(String[] args)
    {
        playGame();
    }

    private static void playGame()
    {
        Cups cups=new Cups("496138527");
        for (int round = 0; round< 10000000; round++)
        {
            playRound(cups);
        }
        Cup one=cups.findOne();
        System.out.println("result");
        System.out.println(one.next.num);
        System.out.println(one.next.next.num);
        System.out.println(((long) one.next.num)*((long) one.next.next.num));
    }

    private static void playRound(final Cups cups)
    {
        Cup taken=cups.remove();
        cups.selectDestination(taken);
        cups.insert(taken);
        cups.selectNewCurrent();
    }

    private static class Cups
    {
        private static final int MAX_NUM = 1000000;
        public Cup current;
        public Cup destination;
        private final Cup[] cupsForNum = new Cup[1000000];

        private Cups(final String cups)
        {
            byte[] b = cups.getBytes();
            Cup last = null;
            Cup first = null;
            for (final byte value : b)
            {
                int num= (char) value-'0';
                Cup next = new Cup(num, last);
                cupsForNum[num-1]=next;
                if (null == first)
                {
                    first = next;
                }
                last = next;
            }
            for (int i = 10; i < 1000000 + 1; i++)
            {
                Cup next = new Cup(i, last);
                cupsForNum[i - 1] = next;
                last = next;
            }
            last.next = first;
            current = first;
        }


        public Cup remove()
        {
            final Cup taken=current.next;
            current.next=skip(current);
            return taken;
        }

        private Cup skip(Cup current)
        {
            for (int i = 0; i< 3; i++)
            {
                current=current.next;
            }
            final Cup result = current.next;
            current.next=null;
            return result;
        }

        public void selectDestination(Cup taken)
        {
            destination=null;
            int nextNum=current.num-1;
            if (0 == nextNum)
            {
                nextNum = MAX_NUM;
            }
            while (isTaken(taken, nextNum))
            {
                nextNum-=1;
                if (0 == nextNum)
                {
                    nextNum = MAX_NUM;
                }
            }
            destination= cupsForNum[nextNum-1 ];
        }

        private boolean isTaken(Cup taken, final int nextNum)
        {
            while (taken!= null)
            {
                if (taken.num==nextNum)
                    return true;
                taken = taken.next;
            }
            return false;
        }

        public void insert(final Cup taken)
        {
            Cup seam=destination.next;
            destination.next=taken;
            Cup last=getLastCup(taken);
            last.next=seam;
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
        public int num;

        public Cup(final int i, final Cup last)
        {
            if (null != last)
            {
                last.next = this;
            }
            num = i;
        }

        @Override
        public String toString()
        {
            return "Cup{" +
                    "num=" + num +
                    ", next=" + (null!=next?next.num:"_") +
                    '}';
        }
    }
}
