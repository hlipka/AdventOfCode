package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day17 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day17().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        int jump=Integer.parseInt(data.getLines().get(0));
        List<Integer> buffer=new ArrayList<>();
        buffer.add(0);
        int num=1;
        int pos=0;
        while (num<2018)
        {
            pos = (pos+jump)%buffer.size();
            buffer.add(pos+1, num);
            pos++;
            num++;
        }
        return buffer.get((pos+1)%buffer.size());
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        int jump = Integer.parseInt(data.getLines().get(0));
        // we use a linked list to implement the ring buffer
        // this is still brute force, but seems reasonably fast to simulate all the buffer manipulation
        // run this with '-Xmx32G -Xms24G' to reduce GC pressure
        // it then takes about 80 seconds
        BufferNode bufferNode=new BufferNode();
        bufferNode.num=0;
        bufferNode.next=bufferNode;
        int num=1;
        final var rounds = isExample?2017:50000000;
        while (num <= rounds)
        {
            for (int i=0;i<jump;i++)
                bufferNode=bufferNode.next;
            BufferNode newNode=new BufferNode();
            newNode.num=num;
            newNode.next=bufferNode.next;
            bufferNode.next=newNode;
            bufferNode=newNode;
            num++;
        }
        if (isExample)
            return bufferNode.next.num;
        while (bufferNode.num!=0)
            bufferNode=bufferNode.next;
        return bufferNode.next.num;
    }

    private static class BufferNode
    {
        int num;
        BufferNode next;
    }
}
