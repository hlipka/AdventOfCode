package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Day08 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day08().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final Iterator<Integer> license = data.getLineAsInteger(" ").iterator();
        Node root=parseLicense(license);
        return root.sumAllMetaData();
    }

    private Node parseLicense(final Iterator<Integer> license)
    {
        Node node=new Node();
        int childNodes=license.next();
        int metaCount=license.next();
        for (int i=0;i<childNodes;i++)
        {
            node.children.add(parseLicense(license));
        }
        for (int i=0;i<metaCount;i++)
        {
            node.metaData.add(license.next());
        }
        return node;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final Iterator<Integer> license = data.getLineAsInteger(" ").iterator();
        Node root = parseLicense(license);
        return root.value();
    }

    private static class Node
    {
        List<Node> children = new ArrayList<>();
        List<Integer> metaData = new ArrayList<>();

        public int sumAllMetaData()
        {
            return children.stream().mapToInt(Node::sumAllMetaData).sum() + metaData.stream().mapToInt(i->i).sum();
        }

        public int value()
        {
            if (children.isEmpty())
            {
                return metaData.stream().mapToInt(i -> i).sum();
            }
            else
            {
                return metaData.stream().mapToInt(m->{
                    if (children.size()<m)
                        return 0;
                    return children.get(m-1).value();
                }).sum();
            }
        }
    }
}
