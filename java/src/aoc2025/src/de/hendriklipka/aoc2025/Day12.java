package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 extends AocPuzzle
{
    int fitCount = 0;

    public Day12()
    {
    }

    public static void main(String[] args)
    {
        new Day12().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        fitCount=0;
        final List<List<String>> problem = data.getStringBlocks();
        final List<String> giftData = problem.removeLast();
        List<CharMatrix> treeData = problem.stream().map(this::parseTree).toList();
        giftData.forEach(line -> handleGift(line, treeData));
        return fitCount;
    }

    private void handleGift(String line, final List<CharMatrix> treeData)
    {
        String[] parts1 = line.split(":");
        String[] parts2 = parts1[0].split("x");
        String[] parts3 = parts1[1].trim().split(" ");
        int width = Integer.parseInt(parts2[0]);
        int height = Integer.parseInt(parts2[1]);
        List<Integer> counts = new ArrayList<>();
        for (String s : parts3)
        {
            Integer parseInt = Integer.parseInt(s.trim());
            counts.add(parseInt);
        }
        int neededSize = 0;

        // count how many grid positions each gift needs as minimum, and from there calculate the minimum space all gifts need
        for (int i = 0; i < counts.size(); i++)
        {
            final int size = getGiftSize(treeData, i);
            neededSize += counts.get(i) * size;
        }

        // check if the tree has enough space
        // it really is as simple as this
        if (width * height >= neededSize)
        {
            fitCount++;
        }
    }

    private static int getGiftSize(final List<CharMatrix> treeData, final int i)
    {
        return treeData.get(i).allMatchingPositions('#').size();
    }

    private CharMatrix parseTree(final List<String> block)
    {
        block.removeFirst();
        final List<List<Character>> cBlock = block.stream().map(l -> l.chars().mapToObj(c -> (char) c).collect(Collectors.toList())).toList();
        final var tree = new CharMatrix(cBlock, '.');
        // fill in actual holes where we cannot place other gifts for sure
        for (Position p : tree.allMatchingPositions('.'))
        {
            if (tree.getNeighbours(p).stream().allMatch(c -> c == '#'))
                tree.set(p, '#');
        }
        return tree;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }
}
