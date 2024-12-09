package de.hendriklipka.aoc2017;

/**
 * User: hli
 */
public class Day03a
{
    public static void main(String[] args)
    {
        int addr = 368078;

        int topLeft = 1;
        int tlDiff = 4;
        int topRight = 1;
        int trDiff = 2;
        int bottomLeft = 1;
        int blDiff = 6;
        int bottomRight = 1;
        int brDiff = 8;
        int steps = 0;

        // the diagonals increase their address with a diff which increases by 8 in each round, but with different start values
        // we loop the rounds until we find the round that holds our value
        while (true)
        {
            if (addr>=bottomRight && addr<(bottomRight+brDiff))
            {
                break;
            }
            topLeft += tlDiff;
            topRight += trDiff;
            bottomLeft += blDiff;
            bottomRight += brDiff;

            tlDiff +=8;
            trDiff +=8;
            blDiff +=8;
            brDiff +=8;
            steps++;
        }
        // the bottom right is the largest number here, because its the one that starts the _next_ round
        System.out.println("steps="+steps);
        topLeft += tlDiff;
        topRight += trDiff;
        bottomLeft += blDiff;
        // find out at which side we are
        if (addr<topRight)
        {
            System.out.println("right side");
        }
        else if (addr<topLeft) // this is the '1024' test number
        {
            System.out.println("top side");
            int topMiddle = (topRight+topLeft)/2;
            System.out.println(""+(steps+Math.abs(addr-topMiddle)+1));
        }
        else if (addr<bottomLeft)
        {
            System.out.println("left side");
        }
        else
        {
            System.out.println("bottom");
            int bottomMiddle = (bottomRight+bottomLeft+brDiff)/2;
            System.out.println("" + (steps + Math.abs(addr - bottomMiddle) + 1));
        }
    }
}
