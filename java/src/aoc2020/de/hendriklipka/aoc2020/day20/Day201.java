package de.hendriklipka.aoc2020.day20;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 20.12.20
 * Time: 13:19
 */
public class Day201
{
    // all images
    private static final List<ImageData> images = new ArrayList<>();

    // map from a border to the images which have this border
    private static final Map<Integer, List<ImageData>> imageMapping = new HashMap<>();

    public static void main(String[] args)
    {
        try
        {
            Iterator<String> lines = new ArrayList<>(FileUtils.readLines(new File("../data/2020/day20.txt"), StandardCharsets.UTF_8)).iterator();
            List<String> currentData=new ArrayList<>();
            while(lines.hasNext())
            {
                String line = lines.next();
                if (StringUtils.isBlank(line))
                {
                    images.add(new ImageData(currentData));
                    currentData = new ArrayList<>();
                }
                else
                {
                    currentData.add(line);
                }
            }
            for (ImageData img: images)
            {
                findNeighbour(img, img.border1);
                findNeighbour(img, img.border2);
                findNeighbour(img, img.border3);
                findNeighbour(img, img.border4);
            }
            long result=1;
            for (ImageData img : images)
            {
                if (img.nCount==2)
                {
                    result*= img.id;
                }
            }
            System.out.println(result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void findNeighbour(final ImageData img, final int border)
    {
        final List<ImageData> imgs = imageMapping.get(border);
        // look whether we find an image for this border which is not ourselves
        for (ImageData otherImage: imgs)
        {
            if (otherImage!=img)
            {
                img.nCount++;
                break;
            }
        }
    }

    private static final Pattern IMG_HEAD=Pattern.compile("Tile (\\d+):");

    private static class ImageData
    {
        List<String> imageData;
        int id;
        int border1;
        int border2;
        int border3;
        int border4;
        public int nCount;

        public ImageData(List<String> data)
        {
            if (11!=data.size())
            {
                throw new IllegalArgumentException("wrong data size: "+data.size());
            }
            String header=data.get(0);
            Matcher m= IMG_HEAD.matcher(header);
            if (!m.matches())
            {
                throw new IllegalArgumentException("invalid header: [" + header+"]");
            }
            id=Integer.parseInt(m.group(1));
            imageData = data.subList(1,data.size());
            calculateBorders();
        }

        private void calculateBorders()
        {
            border1=getBorderNum(imageData.get(0));
            border2 =getBorderNum(imageData.get(9));
            border3 =getBorderNum(imageData, 9);
            border4 =getBorderNum(imageData, 0);
        }

        private int getBorderNum(final String data)
        {
            int border=0;
            int borderR=1<<9;
            for (int i=0;i<10;i++)
            {
                border=border<<1;
                borderR=borderR>>1;
                if ('#'==data.charAt(i))
                {
                    border+=1;
                    borderR+=512;
                }
            }
            addImageMapping(border, borderR);
            return border;
        }

        private void addImageMapping(final int border, final int borderR)
        {
            List<ImageData> imgs1 = imageMapping.computeIfAbsent(border, ArrayList::new);
            List<ImageData> imgs2 = imageMapping.computeIfAbsent(borderR, ArrayList::new);
            imgs1.add(this);
            imgs2.add(this);
        }

        private int getBorderNum(final List<String> data, int offset)
        {
            int border = 0;
            int borderR = 1 << 9;
            for (int i = 0; i < 10; i++)
            {
                border = border << 1;
                borderR = borderR >> 1;
                if ('#' == data.get(i).charAt(offset))
                {
                    border += 1;
                    borderR += 512;
                }
            }
            addImageMapping(border, borderR);
            return border;
        }

        @Override
        public String toString()
        {
            return "ImageData{" +
                    "id=" + id +
                    ", nCount=" + nCount +
                    '}';
        }
    }
}
