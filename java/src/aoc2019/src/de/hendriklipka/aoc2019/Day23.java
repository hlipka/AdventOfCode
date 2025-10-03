package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Day23 extends AocPuzzle
{
    private static final BigInteger M1 = new BigInteger("-1");

    public static void main(String[] args)
    {
        new Day23().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<String> code = data.getFirstLineWords(",");
        BigIntCode node = BigIntCode.fromStringList(code);
        BigIntCode[] nodes=new BigIntCode[50];
        for (int i = 0; i < nodes.length; i++)
        {
            nodes[i]=node.createClone();
        }

        Switch sw = new Switch(nodes, false);

        // start all nodes
        for (final BigIntCode aNode : nodes)
        {
            new Thread(aNode::execute).start();
        }
        // wait until we get the message we need
        while (!sw.isDone())
        {
            synchronized(sw)
            {
                try
                {
                    sw.wait(100);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        // stop all nodes
        for (final BigIntCode intCode : nodes)
        {
            intCode.stopExecution();
        }
        return sw.getMessage();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<String> code = data.getFirstLineWords(",");
        BigIntCode node = BigIntCode.fromStringList(code);
        BigIntCode[] nodes = new BigIntCode[50];
        for (int i = 0; i < nodes.length; i++)
        {
            nodes[i] = node.createClone();
        }

        Switch sw = new Switch(nodes, true);

        // start all nodes
        for (final BigIntCode aNode : nodes)
        {
            new Thread(aNode::execute).start();
        }
        // wait until we get the message we need
        while (!sw.isDone())
        {
            synchronized (M1)
            {
                try
                {
                    M1.wait(10);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("stopping threads");
        // stop all nodes
        for (final BigIntCode intCode : nodes)
        {
            intCode.stopExecution();
        }
        return sw.getMessage();    }

    private static class Switch
    {
        private final Queue<Pair<BigInteger, BigInteger>>[] _outbox;
        private final Boolean[] _readX;
        private final BigInteger[] _xValue;
        private final BigInteger[] _targetNode;
        private final boolean _doNAT;
        private boolean _done;
        private Pair<BigInteger, BigInteger> _natPacket = null;
        private Pair<BigInteger, BigInteger> _lastNatPacket = null;
        private boolean _idle = false;
        private long _idleSince = 0;

        public Switch(final BigIntCode[] nodes, boolean doNAT)
        {
            //noinspection unchecked
            _outbox = new Queue[nodes.length];
            _readX = new Boolean[nodes.length];
            _xValue = new BigInteger[nodes.length];
            _targetNode = new BigInteger[nodes.length];
            for (int i = 0; i < nodes.length; i++)
            {
                final BigIntCode node = nodes[i];
                node.setDoOutput(new InBox(i, this));
                node.setDoInput(new OutBox(i, this));
                _outbox[i] = new ConcurrentLinkedQueue<>();
                _outbox[i].offer(Pair.of(BigInteger.ZERO, new BigInteger(Integer.toString(i))));
                _readX[i] = false;
                _xValue[i] = null;
                _targetNode[i] = null;
            }
            _doNAT = doNAT;
        }

        public synchronized BigInteger getMessage()
        {
            return _lastNatPacket.getRight();
        }

        public synchronized boolean isDone()
        {
            return _done;
        }

        public synchronized void nodeSends(final int sendingNode, final BigInteger value)
        {
            // whenever a node sends something, we are not idle anymore
            if (_targetNode[sendingNode] == null)
            {
                _targetNode[sendingNode] = value;
                _idle = false;
            }
            else if (_xValue[sendingNode] == null)
            {
                _xValue[sendingNode] = value;
                _idle = false;
            }
            else
            {
                _idle = false;
                final var targetNode = this._targetNode[sendingNode].intValue();
                // packet to the NIC
                if (targetNode == 255)
                {
                    // we might come here when we don't stop the nodes fast enough
                    if (!_done)
                    {
                        // store the packet
                        _natPacket = Pair.of(_xValue[sendingNode], value);
                        if (!_doNAT)
                        {
                            // otherwise we stop at the first message
                            _done = true;
                        }
                    }
                    _xValue[sendingNode] = null;
                    _targetNode[sendingNode] = null;
                    return;
                }
                // store the packet ot the outbox
                _outbox[targetNode].offer(Pair.of(_xValue[sendingNode], value));
                _xValue[sendingNode] = null;
                _targetNode[sendingNode] = null;
            }
        }

        public synchronized BigInteger nodeReceives(final int receivingNode)
        {
            final Pair<BigInteger, BigInteger> packet = _outbox[receivingNode].peek();
            if (null == packet)
            {
                if (!_idle)
                {
                    // all packets have been received, and no node is currently sending anything
                    if (Arrays.stream(_outbox).allMatch(Queue::isEmpty) && Arrays.stream(_targetNode).allMatch(Objects::isNull))
                    {
                        _idle = true;
                        _idleSince = System.currentTimeMillis();
                    }
                    // when we detected idle mode, we still return -1 to wait for the next receiver
                    return M1;
                }
                // we are in idle mode, so we place the NATed packet into the node 0 queue
                if (null != _natPacket)
                {
                    _outbox[0].offer(_natPacket);
                    _lastNatPacket = _natPacket;
                    _natPacket = null;
                    // we still return -1, even when this was be node 0
                    return M1;
                }
                // when we are idle for 4 seconds, consider the last packet as the solution
                // (seems we indeed sent some packets multiple times, looks as if our 'idle' state handling is not entirely correct)
                // we might solve this by waiting a bit longer until we declare the network idle (maybe count how often the nodes receive data from an empty
                // queue and use a threshold?)
                if ((System.currentTimeMillis() - _idleSince) > 4000)
                {
                    _done = true;
                }
                // when there is no NAT packet, there is nothing we can do
                return M1;
            }
            // first deliver X
            if (_readX[receivingNode])
            {
                _readX[receivingNode] = false;
                return packet.getLeft();
            }
            // then deliver Y (and then we also remove the packet from the queue)
            _readX[receivingNode] = true;
            _outbox[receivingNode].poll();
            return packet.getRight();
        }
    }

    private record InBox(int nodeNum, Switch theSwitch) implements Consumer<BigInteger>
    {
        @Override
        public void accept(final BigInteger value)
        {
            theSwitch.nodeSends(nodeNum, value);
        }
    }

    private record OutBox(int nodeNum, Switch theSwitch) implements Supplier<BigInteger>
    {
        @Override
        public BigInteger get()
        {
            return theSwitch.nodeReceives(nodeNum);
        }
    }
}
