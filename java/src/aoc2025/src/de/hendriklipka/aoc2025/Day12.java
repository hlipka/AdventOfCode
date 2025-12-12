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
    public static void main(String[] args)
    {
        new Day12().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> problem = data.getStringBlocks();
        final List<String> treeData = problem.removeLast();
        List<CharMatrix> giftShapes = problem.stream().map(this::parseTree).toList();
        List<String> solved=new ArrayList<>();
        List<String> unsolvable=new ArrayList<>();
        List<String> todo=new ArrayList<>();
        // first check how many tress we actually need to solve
        treeData.forEach(line -> handleGift(line, giftShapes, solved, unsolvable, todo));
        if (todo.isEmpty())
            return solved.size();
        return null;
    }

    private void handleGift(final String tree, final List<CharMatrix> giftShapes, final List<String> solved, final List<String> unsolvable,
                            final List<String> todo)
    {
        // parse the tree data
        String[] parts1 = tree.split(":");
        String[] parts2 = parts1[0].split("x");
        String[] parts3 = parts1[1].trim().split(" ");
        int width = Integer.parseInt(parts2[0]);
        int height = Integer.parseInt(parts2[1]);
        List<Integer> counts = new ArrayList<>();
        int totalGifts=0;
        for (String s : parts3)
        {
            Integer parseInt = Integer.parseInt(s.trim());
            counts.add(parseInt);
            totalGifts+=parseInt;
        }
        int neededSize = 0;

        // upper bound: how many 3x3 packages (all gifts are at most this size) can we fit into the area without _any_ packaging / shuffling?
        int boxCount = width / 3 * height / 3;

        // lower bound: how much space do these gifts actually need at least (counting the '#' as used space)?
        for (int i = 0; i < counts.size(); i++)
        {
            final int size = giftShapes.get(i).allMatchingPositions('#').size();
            neededSize += counts.get(i) * size;
        }

        // when the tree has not enough space to fit all the '#' into it, we know this tree will _never_ fit all gifts
        if (width * height < neededSize)
        {
            unsolvable.add(tree);
        }
        // when there are enough 3x3 spaces available, we know we can fit _all_ gifts without further arrangements
        else if (boxCount>=totalGifts)
        {
            solved.add(tree);
        }
        // only when we are in between we need to look at some packaging
        else
        {
            todo.add(tree);
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
