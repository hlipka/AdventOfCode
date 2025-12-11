package de.hendriklipka.aoc.vizualization;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class VizUtils
{
    public static void visualizeGraph(String fileName, Map<String, ? extends VizNode> nodes)
    {
        visualizeGraph(fileName, nodes, null);
    }

    public static void visualizeGraph(String fileName, Map<String, ? extends VizNode> nodes, Function<String, Attributes<ForAll>[]> attrProvider)
    {
        Graph g=graph("graph")
                .directed()
                .graphAttr().with(Rank.dir(Rank.RankDir.BOTTOM_TO_TOP))
                .nodeAttr().with(Font.name("Noto Sans"))
                .linkAttr().with("class", "link-class")
                ;
        List<LinkSource> gn=new LinkedList<>();
        for (VizNode vn: nodes.values())
        {
            Node n=null;
            if (null != attrProvider)
            {
                final var attrs = attrProvider.apply(vn.getNodeName());
                if (null!=attrs)
                {
                    n=node(vn.getNodeName()).with(attrs[0]);
                }
                else
                {
                    n = node(vn.getNodeName());
                }
            }
            else
            {
                n= node(vn.getNodeName());
            }

            for (String tn: vn.getNodeTargets())
            {
                VizNode target=nodes.get(tn);
                n=n.link(target.getNodeName());
            }
            gn.add(n);
        }
        g=g.with(gn);
        try
        {
            Graphviz.fromGraph(g).engine(Engine.DOT).height(2000).render(Format.PNG).toFile(new File(fileName + ".png"));
            Graphviz.fromGraph(g).render(Format.DOT).toFile(new File(fileName+".dot"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
