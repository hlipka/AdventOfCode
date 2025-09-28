package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.CharArrayWorld;
import de.hendriklipka.aoc.search.Graph;
import de.hendriklipka.aoc.search.GraphSearch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day20 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day20().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix space=CharMatrix.fromStringList(data.getLines(), ' ');
        // where the portal entries are (not the characters, the actual entries)
        Map<Position, String> portalLocations=new HashMap<>();
        // find all portals
        final Map<String, Position> portals=new HashMap<>();
        scanForPortals(space, portalLocations, portals);
        // now mark all spaces as walls
        space.allMatchingPositions(' ').forEach(p-> space.set(p, '#'));
        Graph maze=new Graph();
        // all distances within the maze
        final Map<Pair<String, String>, Integer> distances=new HashMap<>();
        calculateDistances(space, portalLocations, distances);
        buildGraph(maze, portals, distances);
        GraphSearch search=new GraphSearch(maze);
        return search.getPathCost("AA", "ZZ");
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // strategy: during portal scan, make sure the out portals are named '1' and the inner '2'
        // when scanning the distances, do not create the graph, but only store the distances
        // create an outer graph, where the '1' portal nodes are skipped (so the '2' nodes do not connect to anything)
        // when creating a graph, prefix? the portal nodes with the layer number (starting at 0)
        // add a first inner graph, and connect its '1' portals to the upper '2' nodes, and skip AA and ZZ
        // the '2' portal nodes here exist, but again are not connected to anything else
        // check whether we can find a way through
        // if not, add another layer with the same strategy and search again
        // this assumes that once we found a way through, adding another layer will not prove a shorter way

        CharMatrix space = CharMatrix.fromStringList(data.getLines(), ' ');
        // where the portal entries are (not the characters, the actual entries)
        Map<Position, String> portalLocations = new HashMap<>();
        // find all portals
        final Map<String, Position> portals = new HashMap<>();
        scanForPortals(space, portalLocations, portals);
        // now mark all spaces as walls
        space.allMatchingPositions(' ').forEach(p -> space.set(p, '#'));
        // all distances within the maze
        final Map<Pair<String, String>, Integer> distances = new HashMap<>();
        calculateDistances(space, portalLocations, distances);

        Graph maze = new Graph();
        buildOuterGraph(maze, portals, distances);
        addInnerGraphForLevel(maze, portals, distances, 1);
        int currentLevel=1;
        while (true)
        {
            GraphSearch search = new GraphSearch(maze);
            int cost = search.getPathCost("AA", "ZZ");
            if (cost<1000000)
                return cost;
            currentLevel++;
            System.out.println("adding another level: "+currentLevel);
            addInnerGraphForLevel(maze, portals, distances, currentLevel);
        }
    }

    private void buildGraph(final Graph maze, final Map<String, Position> portals, final Map<Pair<String, String>, Integer> distances)
    {
        // add all portals as nodes
        portals.keySet().forEach(maze::addNode);
        // add all paths within the maze
        for (Pair<String, String> path: distances.keySet())
        {
            maze.addEdge(path.getLeft(), path.getRight(), distances.get(path));
        }
        // add the portals itself as edges
        portals.keySet().forEach(p->
        {
            if (p.endsWith("1"))
            {
                maze.addEdge(p, p.substring(0, 2)+"2", 1);
            }
        });
    }

    private void buildOuterGraph(final Graph maze, final Map<String, Position> portals,
                                 final Map<Pair<String, String>, Integer> distances)
    {
        // add all portals as nodes
        for (String s : portals.keySet())
        {
            maze.addNode(getNameForLevel(s, "0"));
        }
        // add all paths within the maze
        for (Pair<String, String> path: distances.keySet())
        {
            maze.addEdge(getNameForLevel(path.getLeft(), "0"), getNameForLevel(path.getRight(), "0"), distances.get(path));
        }
        // but the portals itself are not connected
    }

    private void addInnerGraphForLevel(final Graph maze, final Map<String, Position> portals,
                                       final Map<Pair<String, String>, Integer> distances, final int currentLevel)
    {
        final var level = Integer.toString(currentLevel);
        final var upperLevel = Integer.toString(currentLevel - 1);
        // add the nodes for the next level
        for (String s : portals.keySet())
        {
            if (!s.equals("AA") && !s.equals("ZZ"))
            {
                maze.addNode(getNameForLevel(s, level));
            }
            // connect the outer portals ('1') to the inner portals ('2') of the upper level
            if (s.endsWith("1"))
            {
                maze.addEdge(getNameForLevel(s, level), getNameForLevel(s.substring(0, 2)+"2", upperLevel), 1);
            }
        }
        // add all paths within the maze
        for (Pair<String, String> path: distances.keySet())
        {
            if (!path.getLeft().equals("AA") && !path.getLeft().equals("ZZ") && !path.getRight().equals("AA") && !path.getRight().equals("ZZ"))
                maze.addEdge(getNameForLevel(path.getLeft(), level), getNameForLevel(path.getRight(), level), distances.get(path));
        }
    }

    private static String getNameForLevel(String name, final String level)
    {
        if (name.equals("AA") || name.equals("ZZ"))
        {
            return name;
        }
        return level + name;
    }

    private void calculateDistances(final CharMatrix space, final Map<Position, String> portalLocations, final Map<Pair<String, String>, Integer> distances)
    {
        Set<String> visited=new HashSet<>();
        for (Position portal: portalLocations.keySet())
        {
            String portalName=portalLocations.get(portal);
            // then, find all other reachable portals and their distances
            AStarSearch search = new AStarSearch(new CharArrayWorld(space, portal, new Position(0,0), '#'));
            search.findPath();
            for (Position other: portalLocations.keySet())
            {
                if (!other.equals(portal))
                {
                    int dist=search.getPathLength(other);
                    if (dist<1000000)
                    {
                        String otherName=portalLocations.get(other);
                        // did we already store the other direction?
                        if (visited.contains(otherName +portalName))
                            continue;
                        distances.put(Pair.of(portalName,otherName), dist);
                        visited.add(portalName +otherName);
                    }
                }
            }
        }
    }

    private void scanForPortals(final CharMatrix space, final Map<Position, String> portalLocations, final Map<String, Position> portals)
    {
        // we know that portals are written top-to-bottom and left-to-right, so the first character we find will be also the first of the portal
        for (Position p: space.allPositions())
        {
            char c=space.at(p);
            if (c>='A'&&c<='Z')
            {
                // also, remove the portal from the data so its excluded for the rest of the search (and also when doing at the distance search)
                space.set(p, ' ');
                String portalName;
                Position portalPos;
                char c1=space.at(p.updated(Direction.RIGHT));
                char c2=space.at(p.updated(Direction.DOWN));
                if (c1>='A' && c1<='Z') // portal is across
                {
                    portalName=""+c+c1;
                    // to the left or to the right?
                    if (space.at(p.updated(Direction.LEFT))=='.')
                    {
                        portalPos=p.updated(Direction.LEFT);
                    }
                    else
                    {
                        portalPos=p.updated(Direction.RIGHT).updated(Direction.RIGHT);
                    }
                    space.set(p.updated(Direction.RIGHT), ' ');
                }
                else // downwards
                {
                    portalName = "" + c + c2;
                    // up or down?
                    if (space.at(p.updated(Direction.UP)) == '.')
                    {
                        portalPos = p.updated(Direction.UP);
                    }
                    else
                    {
                        portalPos = p.updated(Direction.DOWN).updated(Direction.DOWN);
                    }
                    space.set(p.updated(Direction.DOWN), ' ');
                }
                //noinspection ConstantValue
                if (!portalName.equals("AA") && !portalName.equals("ZZ"))
                {
                    // name the outer portals '1'
                    if (portalPos.row<3 || portalPos.col<3 || portalPos.row>(space.rows()-4) || portalPos.col>space.cols()-4)
                    {
                        portalName += "1";
                    }
                    else
                    {
                        portalName += "2";
                    }
                }
                portalLocations.put(portalPos, portalName);
                portals.put(portalName, portalPos);
            }
        }
    }
}
