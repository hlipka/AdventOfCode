package de.hendriklipka.aoc.vizualization;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SwingViewer
{
    public SwingViewer(Consumer<Graphics2D> drawer)
    {
        final JFrame frame = new JFrame("AoC");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ShapeDrawing drawing = new ShapeDrawing(drawer);
        frame.getContentPane().add(drawing);
        frame.setVisible(true);
    }

    private static class ShapeDrawing extends JComponent
    {
        private final Consumer<Graphics2D> _drawer;

        public ShapeDrawing(final Consumer<Graphics2D> drawer)
        {
            _drawer = drawer;
        }

        public void paint(Graphics g)
        {
            _drawer.accept((Graphics2D)g);
        }
    }
}
