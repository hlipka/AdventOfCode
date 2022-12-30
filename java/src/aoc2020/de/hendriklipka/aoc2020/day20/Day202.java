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
public class Day202
{
    // all images
    private static final List<TileData> allTiles = new ArrayList<>();

    // map from a border to the images which have this border
    // for each image the border is compiled into an integer so we can search faster
    private static final Map<Integer, List<TileData>> borderTileMapping = new HashMap<>();

    public static void main(String[] args)
    {
        try
        {
            getTileData();
            // find the first corner tile
            TileData topLeft=null;
            for (TileData img : allTiles)
            {
                if (img.neighbours ==2)
                {
                    topLeft=img;
                    break;
                }
            }
            // rotate it so it is the top left image (top and left border are not
            while (topLeft.getLeftBorder()!=-1 && topLeft.getRightBorder()!=-1)
            {
                topLeft.rotate90right();
            }

            List<List<TileData>> picture = new ArrayList<>();
            List<TileData> currentRow = new ArrayList<>();
            currentRow.add(topLeft);
            picture.add(currentRow);

            // now fill all rows from left to right, while rotating / flipping the tiles into the correct position
            TileData lastImage = topLeft;
            while (true)
            {
                while (lastImage.getRightBorder()!=-1)
                {
                    int border=lastImage.getRightBorder();
                    TileData nextImage = getTileForBorder(border, lastImage);
                    nextImage.rotateBorderToLeft(border);
                    currentRow.add(nextImage);
                    lastImage=nextImage;
                }
                // are we at the bottom right?
                if (-1==lastImage.getBottomBorder() && -1==lastImage.getRightBorder())
                    break;
                // if not, we look for the image below the left-most image from the last row and start a new row
                lastImage=currentRow.get(0);
                int border = lastImage.getBottomBorder();
                TileData nextImage = getTileForBorder(border, lastImage);
                nextImage.rotateBorderToTop(border);
                currentRow = new ArrayList<>();
                picture.add(currentRow);
                currentRow.add(nextImage);
                lastImage=nextImage;
            }
            for (TileData img : allTiles)
            {
                img.stripBorder();
            }
            // with the borders removed, assemble the complete picture
            // the picture is stored as one-dimensional array so the regex search is a bit faster
            final int rowSize = 8 * picture.get(0).size();
            final int pictureSize = 8 * picture.size() * rowSize;

            char[] wholePicture=new char[pictureSize];
            int pos=0;
            for (List<TileData> row: picture)
            {
                for (int r=0;r<8;r++)
                {
                    for (TileData img : row)
                    {
                        String s=img.getRow(r);
                        for (byte c: s.getBytes())
                        {
                            wholePicture[pos++]=(char)c;
                        }
                    }
                }
            }

            // we use a cloned image for marking the found sea monsters, to avoid skipping overlapping monsters when they are already marked
            char[] cleanedPicture=wholePicture.clone();
            String[] pattern={"                  # ","#    ##    ##    ###", " #  #  #  #  #  #   "};
            // create a pattern which spans multiple lines in the wholePicture structure
            String fullPattern=pattern[0]+StringUtils.repeat(' ', rowSize-20)+pattern[1] + StringUtils.repeat(' ', rowSize - 20) + pattern[2];
            fullPattern=fullPattern.replace(' ', '.');

            Pattern seaMonster=Pattern.compile(fullPattern);

            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture=rotatePicture(wholePicture);
            cleanedPicture=rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            // back to the start
            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);

            // flip, and rotate again
            wholePicture = flipPicture(wholePicture);
            cleanedPicture = flipPicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            wholePicture = rotatePicture(wholePicture);
            cleanedPicture = rotatePicture(cleanedPicture);
            searchMonster(wholePicture, cleanedPicture, seaMonster, fullPattern);

            int count=0;
            for (final char c : cleanedPicture)
            {
                if (c == '#')
                    count++;
            }
            System.out.println("waves left "+count);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static char[] flipPicture(final char[] picture)
    {
        char[] result = new char[picture.length];
        for (int row = 0; row < 96; row++)
            for (int col = 0; col < 96; col++)
                result[col + row * 96] = picture[(95-col) + row * 96];
        return result;
    }

    private static char[] rotatePicture(final char[] picture)
    {
        char[] result=new char[picture.length];
        for (int row=0;row<96;row++)
            for (int col=0;col<96;col++)
                result[col+row*96]=picture[col*96+(95-row)];
        return result;
    }

    private static void searchMonster(final char[] picture, final char[] cleanedPicture, final Pattern monster, final String fullPattern)
    {
        Matcher m=monster.matcher(new String(picture));
        // there might be overlapping matches, so we start at the last match position+1
        int lastPos=-1;
        while (m.find(lastPos+1))
        {
            final int matchPos = m.start();
            lastPos=matchPos;
            int col=matchPos%96;
            if (col<=96- 20)
            {
                cleanPicture(cleanedPicture, matchPos, fullPattern);
            }
        }
    }

    private static void cleanPicture(final char[] picture, final int pos, final String pattern)
    {
        for (int offset=0;offset<pattern.length();offset++)
        {
            if (pattern.charAt(offset)=='#')
            {
                final char c = picture[pos + offset];
                if (c=='#' || c == 'O')
                {
                    picture[pos + offset] = 'O';
                }
                else // sanity check, we must not replace non-waves
                {
                    throw new IllegalStateException("trying to replace something that is not part of the monster");
                }
            }
        }
    }

    private static TileData getTileForBorder(final int border, TileData tile)
    {
        final List<TileData> tiles = borderTileMapping.get(border);
        if (tiles.size()!=2)
        {
            throw new IllegalStateException("wrong mapping content for ["+border+"]:  "+tiles);
        }
        // look whether we find an tile for this border which is not ourselves
        for (TileData otherTile : tiles)
        {
            if (otherTile != tile)
            {
                return otherTile;
            }
        }
        return null;
    }

    private static void getTileData() throws IOException
    {
        Iterator<String> lines = new ArrayList<>(FileUtils.readLines(new File("data/day20.txt"), StandardCharsets.UTF_8)).iterator();
        List<String> currentData=new ArrayList<>();
        while(lines.hasNext())
        {
            String line = lines.next();
            if (StringUtils.isBlank(line))
            {
                final TileData tile = new TileData(currentData);
                tile.calculateBorders(borderTileMapping);
                allTiles.add(tile);
                currentData = new ArrayList<>();
            }
            else
            {
                currentData.add(line);
            }
        }
        final TileData lastTile = new TileData(currentData);
        lastTile.calculateBorders(borderTileMapping);
        allTiles.add(lastTile);

        // fix the borders (count them, set non-matching ones to -1)
        for (TileData tile: allTiles)
        {
            for (int i=0;i<4;i++)
            {
                if (!findNeighbour(tile, tile.borders[i]))
                {
                    // mark the tiles border which are at the border of the whole image
                    tile.borders[i]=-1;
                }
            }
        }
    }

    private static boolean findNeighbour(final TileData tile, final int border)
    {
        final List<TileData> tiles = borderTileMapping.get(border);
        // look whether we find an tile for this border which is not ourselves
        for (TileData otherTile: tiles)
        {
            if (otherTile!=tile)
            {
                tile.neighbours++;
                return true;
            }
        }
        return false;
    }


}
