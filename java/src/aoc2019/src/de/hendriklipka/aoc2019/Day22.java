package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class Day22 extends AocPuzzle
{

    private static final BigInteger M1 = new BigInteger("-1");

    public static void main(String[] args)
    {
        new Day22().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Pair<Integer, Integer>> instructions = data.getLines().stream().map(this::parse).toList();
        return shuffleDeck(instructions, 2019, 10007);
    }

    // the shuffle rules are so that each card is independent of each other
    // so it is possible to track each card on its own
    private long shuffleDeck(final List<Pair<Integer, Integer>> instructions, long cardPos, final long cardCount)
    {
        long pos=cardPos;
        for (Pair<Integer, Integer> instr : instructions)
        {
            pos=shuffleCards(pos, instr, cardCount);
        }
        return pos;
    }

    private long shuffleCards(final long cardPos, final Pair<Integer, Integer> instr, long cardCount)
    {
        return switch (instr.getLeft())
        {
            case 0 -> cardCount - cardPos - 1;
            case 1 -> cutCards(cardPos, instr.getRight(), cardCount);
            case 2 -> dealCards(cardPos, instr.getRight(), cardCount);
            default -> throw new IllegalArgumentException("unknown instruction: " + instr);
        };
    }

    private long cutCards(final long cardPos, long n, long cardCount)
    {
        // find out where the cut actually is
        if (n < 0)
        {
            n = cardCount + n;
        }
        if (cardPos >= n)
        {
            return cardPos - n;
        }
        return cardPos + (cardCount - n);
    }

    private long dealCards(final long cardPos, long n, long cardCount)
    {
        return (cardPos*n)%cardCount;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // two observations:
        // - the shuffling loops after n-1 rounds (the deck is then sorted again)
        // - each of the instructions is, essentially, a linear function:
        //   - newPos1 = a1 * oldPos + b1
        //   - so two consecutive instructions can be combined into a new linear function
        //   - newPos2 = a2 * (a1 * oldPos + b1) + b2
        //     -> newPos2 = a2 * a1 * oldPos + a2 * b1 + b2
        //   - each of these functions is obviously MOD deck_size
        //   - and so on until we have one function for the full instructions list
        // - so we can then combine consecutive rounds of shuffling again into a new function
        // - and with that create a function for an arbitrary round
        // - the ask for part 2 is essentially to go backwards (reverse shuffle), but with the first observation we know
        //   that we can achieve the same by going (deck_size-1-rounds) shuffles forward

        // read the instructions
        final List<Pair<Integer, Integer>> instructions = data.getLines().stream().map(this::parse).toList();

        // combine them into one formula (note: this needs BigInteger to avoid overflows)
        BigInteger cardCount = new BigInteger("10007");
        Pair<BigInteger, BigInteger> factorsSmall=Pair.of(BigInteger.ONE, BigInteger.ZERO);
        for (Pair<Integer, Integer> instr : instructions)
        {
            factorsSmall = switch (instr.getLeft())
            {
                case 0 -> doReverse(factorsSmall.getLeft(), factorsSmall.getRight(), instr.getRight(), cardCount);
                case 1 -> doCut(factorsSmall.getLeft(), factorsSmall.getRight(), instr.getRight(), cardCount);
                case 2 -> doDeal(factorsSmall.getLeft(), factorsSmall.getRight(), instr.getRight(), cardCount);
                default -> throw new IllegalStateException("Unexpected instruction: " + instr.getLeft());
            };
        }
        // verification - do the same calculation as part 1
        System.out.println(doCalculate(factorsSmall.getLeft(), factorsSmall.getRight(), new BigInteger("2019"), cardCount));

        // now the same for part 2
        cardCount = new BigInteger("119315717514047");
        // we do not calculate the required rounds backwards (which would require inverting all calculations), but instead forwards,
        // and since the loop is one shorter than the number of cards we need to skip one round
        long rounds = 119315717514047L - 101741582076661L - 1L;

        System.out.println("res -1: " + getResult(instructions, cardCount, rounds));
        // 85610492136105 is too high
        return -1;
    }

    private BigInteger getResult(final List<Pair<Integer, Integer>> instructions, final BigInteger cardCount, final long rounds)
    {
        Pair<BigInteger, BigInteger> factors = Pair.of(BigInteger.ONE, BigInteger.ZERO);
        for (Pair<Integer, Integer> instr : instructions)
        {
            factors = switch (instr.getLeft())
            {
                case 0 -> doReverse(factors.getLeft(), factors.getRight(), instr.getRight(), cardCount);
                case 1 -> doCut(factors.getLeft(), factors.getRight(), instr.getRight(), cardCount);
                case 2 -> doDeal(factors.getLeft(), factors.getRight(), instr.getRight(), cardCount);
                default -> throw new IllegalStateException("Unexpected instruction: " + instr.getLeft());
            };
        }

        // combine these into a single formula
        // - square the formula with itself until we have all squares up to the largest b it that is set into the number of target rounds
        // - then combine the squares we need together (use the set bits of the target round number)
        //   - pos = a * (a * pos + b) + b
        //     -> pos = a^2 * pos + (a + 1) * b
        BigInteger rb=new BigInteger(Long.toString(rounds));
        int bitNum=rb.bitLength();
        Pair<BigInteger, BigInteger>[] allFactors=new Pair[bitNum];
        allFactors[0]=factors;
        for (int i = 1; i < bitNum; i++)
        {
            allFactors[i]=combine(allFactors[i-1].getLeft(), allFactors[i - 1].getRight(), allFactors[i - 1].getLeft(), allFactors[i - 1].getRight()    ,
                    cardCount);
        }
        Pair<BigInteger, BigInteger> finalFactors = Pair.of(BigInteger.ONE, BigInteger.ZERO);
        for (int i=0; i<bitNum; i++)
        {
            if ( rb.testBit(i))
            {
                finalFactors=combine(finalFactors.getLeft(), finalFactors.getRight(), allFactors[i].getLeft(), allFactors[i].getRight(), cardCount);
            }
        }
        final BigInteger result = doCalculate(finalFactors.getLeft(), finalFactors.getRight(), new BigInteger("2020"), cardCount);
        return result;
    }

    private BigInteger doCalculate(final BigInteger a, final BigInteger b, final BigInteger num, final BigInteger cardCount)
    {
        return a.multiply(num).add(b).mod(cardCount);
    }

    private Pair<BigInteger, BigInteger> doCut(final BigInteger a1, final BigInteger b1, final Integer num, final BigInteger cardCount)
    {
        // cut = (num + cardCount) % cardCount
        // pos = (pos - cut + cardCount) % cardCount
        BigInteger cut = new BigInteger(Integer.toString(num)).add(cardCount).mod(cardCount);
        return combine(a1, b1, BigInteger.ONE, cardCount.subtract(cut), cardCount);
    }

    private Pair<BigInteger, BigInteger> doDeal(final BigInteger a1, final BigInteger b1, final Integer num, final BigInteger cardCount)
    {
        // pos = pos * num % cardCount
        return combine(a1, b1, new BigInteger(Integer.toString(num)), BigInteger.ZERO, cardCount);
    }

    private Pair<BigInteger, BigInteger> doReverse(final BigInteger a1, final BigInteger b1, final Integer num, final BigInteger cardCount)
    {
        // pos = cardCount -1 - pos
        // pos = -1 * pos + (cardCount-1)
        return combine(a1, b1, M1, cardCount.subtract(BigInteger.ONE), cardCount);
    }

    private Pair<BigInteger, BigInteger> combine(final BigInteger a1, final BigInteger b1, final BigInteger a2, final BigInteger b2, final BigInteger cardCount)
    {
        // see above -> newPos2 = a2 * a1 * oldPos + a2 * b1 + b2
        return Pair.of(a1.multiply(a2).mod(cardCount), a2.multiply(b1).add(b2).mod(cardCount));
    }

    private Pair<Integer, Integer> parse(String instr)
    {
        if (instr.startsWith("deal into"))
        {
            // 10 cards means positions 0..9, so don't forget the -1
            return Pair.of(0,0);
        }
        else if (instr.startsWith("cut"))
        {
            int n = AocParseUtils.parseIntFromString(instr, "cut (-?\\d+)");
            return Pair.of(1, n);
        }
        else if (instr.startsWith("deal with"))
        {
            int n = AocParseUtils.parseIntFromString(instr, "deal with increment (\\d+)");
            return Pair.of(2,n);
        }
        throw new IllegalArgumentException("unknown instruction: " + instr);
    }
}
