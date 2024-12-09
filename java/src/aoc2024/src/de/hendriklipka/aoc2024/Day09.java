package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day09 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day09().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> diskData = data.getLinesAsDigits().get(0);
        int[] disk = createDiskImage(diskData);
        compactBlocks(diskData, disk);
        return getDiskChecksum(disk);
    }

    private static void compactBlocks(final List<Integer> diskData, final int[] disk)
    {
        int freeStart = diskData.get(0);
        int lastBlock = disk.length - 1;
        while (freeStart < lastBlock)
        {
            if (disk[freeStart] != -1)
            {
                freeStart++;
            }
            else if (disk[lastBlock] == -1)
            {
                lastBlock--;
            }
            else
            {
                disk[freeStart] = disk[lastBlock];
                disk[lastBlock]=-1;
                freeStart++;
                lastBlock--;
            }
        }
    }

    private static int getDiskSize(final List<Integer> diskData, int diskSize)
    {
        for (int i = 0; i < diskData.size(); i++)
        {
            final Integer blockDesc = diskData.get(i);
            diskSize +=blockDesc;
        }
        return diskSize;
    }

    private static long getDiskChecksum(final int[] disk)
    {
        // calculate the checksum
        long result = 0;
        for (int i = 0; i < disk.length; i++)
        {
            if (-1 != disk[i])
            {
                result+= i * disk[i];
            }
        }
        return result;
    }

    private static int[] createDiskImage(final List<Integer> diskData)
    {
        int[] disk=new int[getDiskSize(diskData, 0)];
        int ofs=0;
        int blockNum=0;
        // fill in all block number / mark free space
        for (int i = 0; i < diskData.size(); i++)
        {
            final Integer blockDesc = diskData.get(i);
            if (0 == i % 2)
            {
                Arrays.fill(disk, ofs, ofs + blockDesc, blockNum);
                blockNum++;
                ofs+= blockDesc;
            }
            else
            {
                Arrays.fill(disk, ofs, ofs + blockDesc, -1);
                ofs+= blockDesc;
            }
        }
        return disk;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> diskData = data.getLinesAsDigits().get(0);
        List<MutablePair<Integer, Integer>> disk =new ArrayList<>();
        int blockNum=0;
        // create a block list from the description
        for (int i=0;i<diskData.size();i++)
        {
            final Integer blockSize = diskData.get(i);
            if (0==blockSize)
                continue;
            if (0==i%2)
            {
                disk.add(MutablePair.of(blockNum, blockSize));
                blockNum++;
            }
            else
            {
                disk.add(MutablePair.of(-1, blockSize));
            }
        }
        // look for the file in descending order
        for (int currentFile = blockNum-1; currentFile>=0; currentFile--)
        {
            final int fileNum=currentFile;
            // first, find the block for the current file
            final MutablePair<Integer, Integer> fileDesc = disk.stream().filter(p -> p.getLeft() == fileNum).findFirst().get();
            int fileSize=fileDesc.getRight();
            // look through all blocks to find the first free block which fits the file
            // (no need to merge free block - we never will look at the blocks freed by moving a file again
            for (int currentBlockNum = 0; currentBlockNum < disk.size(); currentBlockNum++)
            {
                final MutablePair<Integer, Integer> currentBlock = disk.get(currentBlockNum);
                if (currentBlock.getLeft() == -1) // if it is a free block
                {
                    // when it is an exact fit, we can just swap contents
                    if ( currentBlock.getRight() == fileSize)
                    {
                        currentBlock.setLeft(fileDesc.getLeft());
                        fileDesc.setLeft(-1);
                        break;
                    }
                    // when the free block is larger than the file, we move the file and add a new free block afterwards
                    else if ( currentBlock.getRight() >= fileSize)
                    {
                        int remaining=currentBlock.getRight()-fileSize;
                        currentBlock.setLeft(fileDesc.getLeft());
                        currentBlock.setRight(fileSize);
                        disk.add(currentBlockNum+1, new MutablePair<>(-1, remaining));
                        fileDesc.setLeft(-1);
                        break;
                    }
                }
                else // this is a file
                {
                    // when we reach the current file, we stop looking for free space
                    if (currentBlock.getLeft() == fileDesc.getLeft())
                    {
                        break;
                    }
                }
            }
        }
        long result=0;
        int currentBlockNum=0;
        for (Pair<Integer, Integer> block : disk)
        {
            if (-1==block.getLeft())
            {
                currentBlockNum+=block.getRight();
            }
            else
            {
                for (int i=0;i<block.getRight();i++)
                {
                    result += block.getLeft()*currentBlockNum;
                    currentBlockNum++;

                }
            }
        }
        return result;
    }
}
