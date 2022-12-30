package de.hendriklipka.aoc2020.day20;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 20.12.20
 * Time: 20:05
 */
class TileData
{
    private static final Pattern TILE_HEAD = Pattern.compile("Tile (\\d+):");

    List<String> tileData;
    int id;
    // int representing the border, in normalized for (each corner has a MSB and a LSB), so we can rotate the tile
    int[] borders = new int[4];
    public int neighbours;

    public TileData(List<String> data)
    {
        if (11 != data.size())
        {
            throw new IllegalArgumentException("wrong data size: " + data.size());
        }
        String header = data.get(0);
        Matcher m = TILE_HEAD.matcher(header);
        if (!m.matches())
        {
            throw new IllegalArgumentException("invalid header: [" + header + "]");
        }
        id = Integer.parseInt(m.group(1));
        tileData = data.subList(1, data.size());
    }

    void calculateBorders(final Map<Integer, List<TileData>> tileMapping)
    {
        borders[0] = getBorderNum(tileData.get(0), false, tileMapping);
        borders[1] = getBorderNum(tileData, 9, false, tileMapping);
        borders[2] = getBorderNum(tileData.get(9), true, tileMapping);
        borders[3] = getBorderNum(tileData, 0, true, tileMapping);
    }

    private int getBorderNum(final String data, boolean useReversed, Map<Integer, List<TileData>> tileMapping)
    {
        int border = 0;
        for (int i = 0; i < 10; i++)
        {
            border = border << 1;
            if ('#' == data.charAt(i))
            {
                border += 1;
            }
        }
        int borderR=reverseBorderNum(border);
        addTileMapping(border, borderR, tileMapping);
        return useReversed ? borderR : border;
    }

    private void addTileMapping(final int border, final int borderR, Map<Integer, List<TileData>> tileMapping)
    {
        List<TileData> imgs1 = tileMapping.computeIfAbsent(border, ArrayList::new);
        List<TileData> imgs2 = tileMapping.computeIfAbsent(borderR, ArrayList::new);
        imgs1.add(this);
        imgs2.add(this);
    }

    private int getBorderNum(final List<String> data, int offset, boolean useReversed, Map<Integer, List<TileData>> tileMapping)
    {
        int border = 0;
        for (int i = 0; i < 10; i++)
        {
            border = border << 1;
            if ('#' == data.get(i).charAt(offset))
            {
                border += 1;
            }
        }
        int borderR = reverseBorderNum(border);
        addTileMapping(border, borderR, tileMapping);
        return useReversed ? borderR : border;
    }

    private int getTopBorder()
    {
        return borders[0];
    }

    int getBottomBorder()
    {
        return borders[2];
    }

    int getRightBorder()
    {
        return borders[1];
    }


    int getLeftBorder()
    {
        return borders[3];
    }

    @Override
    public String toString()
    {
        return "TileData{" +
                "id=" + id +
                ", nCount=" + neighbours +
                ", t=" + borders[0] + "/"+reverseBorderNum(borders[0]) +
                ", r=" + borders[1] + "/" + reverseBorderNum(borders[1]) +
                ", b=" + borders[2] + "/" + reverseBorderNum(borders[2]) +
                ", l=" + borders[3] + "/" + reverseBorderNum(borders[3]) +
                '}';
    }

    public String getRow(int row)
    {
        return tileData.get(row);
    }

    public void stripBorder()
    {
        tileData.remove(0);
        tileData.remove(8);
        for (int i = 0; i < 8; i++)
        {
            tileData.set(i, tileData.get(i).substring(1, 9));
        }
    }

    public void rotate90right()
    {
        List<String> newData = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            StringBuilder sb = new StringBuilder();
            for (int j = 9; j >= 0; j--)
            {
                sb.append(tileData.get(j).charAt(i));
            }
            newData.add(sb.toString());
        }
        tileData = newData;
        int old3 = borders[3];
        borders[3] = borders[2];
        borders[2] = borders[1];
        borders[1] = borders[0];
        borders[0] = old3;
    }

    public void flipTopBottom()
    {
        Collections.reverse(tileData);
        reverseBorder(1);
        reverseBorder(3);
        int old0=borders[0];
        // since the border numbers are normalized, we need to reverse them when flipping the tile
        borders[0]=reverseBorderNum(borders[2]);
        borders[2]= reverseBorderNum(old0);
    }

    public void flipLeftRight()
    {
        for (int i = 0; i < 10; i++)
        {
            tileData.set(i, StringUtils.reverse(tileData.get(i)));
        }
        reverseBorder(0);
        reverseBorder(2);
        int old1=borders[1];
        // since the border numbers are normalized, we need to reverse them when flipping the tile
        borders[1] = reverseBorderNum(borders[3]);
        borders[3] = reverseBorderNum(old1);
    }

    // returns the bit-reversed number for a border
    private void reverseBorder(int idx)
    {
        borders[idx] = reverseBorderNum(borders[idx]);
    }

    private int reverseBorderNum(int border)
    {
        if (-1==border)
            return border;
        int result = 0;
        for (int i = 0; i < 10; i++)
        {
            result = result << 1;
            result += (border & 1);
            border = border >> 1;
        }
        return result;
    }

    public void rotateBorderToLeft(final int border)
    {
        int revBorder = reverseBorderNum(border);
        while (border != getLeftBorder() && revBorder != getLeftBorder())
        {
            rotate90right();
        }
        // matches must always be in reverse, so when the borders matched directly we need to flip the image
        if (border == getLeftBorder())
        {
            flipTopBottom();
        }
    }

    public void rotateBorderToTop(final int border)
    {
        int revBorder = reverseBorderNum(border);
        while (border != getTopBorder() && revBorder != getTopBorder())
        {
            rotate90right();
        }
        // matches must always be in reverse, so when the borders matched directly we need to flip the image
        if (border == getTopBorder())
        {
            flipLeftRight();
        }
    }
}
