package de.hendriklipka.aoc.vizualization;

import java.util.Collection;

public interface VizNode
{
    String getNodeName();
    Collection<String> getNodeTargets();
}
