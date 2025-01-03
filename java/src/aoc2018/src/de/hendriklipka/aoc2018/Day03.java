package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day03 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day03().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final IntMatrix fabric=new IntMatrix(1100, 1100, 0);
        data.getLines().stream().map(this::parseClaim).forEach(c->doClaim(c, fabric));

        return fabric.count(c->c>1);
    }

    private void doClaim(final Claim claim, final IntMatrix fabric)
    {
        for (int r=0;r<claim.height; r++)
        {
            for (int c=0;c<claim.width;c++)
            {
                Position p = new Position(claim.top+r, claim.left+c);
                fabric.set(p, fabric.at(p)+1);
            }
        }
    }

    private Claim parseClaim(final String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");
        Claim claim = new Claim();
        claim.id=Integer.parseInt(parts.get(0));
        claim.left=Integer.parseInt(parts.get(1));
        claim.top=Integer.parseInt(parts.get(2));
        claim.width=Integer.parseInt(parts.get(3));
        claim.height=Integer.parseInt(parts.get(4));
        return claim;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final IntMatrix fabric = new IntMatrix(1100, 1100, 0);
        final List<Claim> claims = data.getLines().stream().map(this::parseClaim).toList();
        Set<Integer> tainted = new HashSet<>();
        // we check each claim whether it collides with another claim, and record all tainted claims
        claims.forEach(c -> checkClaim(c, fabric, tainted));
        // there is just one left
        return CollectionUtils.subtract(claims.stream().map(c->c.id).toList(), tainted).iterator().next();
    }

    private void checkClaim(final Claim claim, final IntMatrix fabric, final Set<Integer> tainted)
    {
        for (int r = 0; r < claim.height; r++)
        {
            for (int c = 0; c < claim.width; c++)
            {
                Position p = new Position(claim.top + r, claim.left + c);
                final var existing = fabric.at(p);
                if (existing == 0)
                { // not claimed so far, so set a claim on it
                    fabric.set(p, claim.id);
                }
                if (existing > 0)
                { // already claimed, so we add both to our list
                    tainted.add(existing);
                    tainted.add(claim.id);
                    // we do not set anything here, both claims are tainted
                }
            }
        }
    }

    private static class Claim
    {
        int id, left, top, width, height;
    }
}
