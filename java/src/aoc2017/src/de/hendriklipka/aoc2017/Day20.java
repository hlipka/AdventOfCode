package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

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
        List<Particle> particles = new ArrayList<>();
        List<String> lines = data.getLines();
        for (int i = 0; i < lines.size(); i++)
        {
            final String s = lines.get(i);
            Particle particle = new Particle(i, s);
            particles.add(particle);
        }
        // we just need the particle with the lowest acceleration, it will stay closets to the origin in the long run
        return particles.stream().min(Comparator.comparingLong(Particle::accel)).orElseThrow().num;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Particle> particles = new ArrayList<>();
        List<String> lines = data.getLines();
        for (int i = 0; i < lines.size(); i++)
        {
            final String s = lines.get(i);
            Particle particle = new Particle(i, s);
            particles.add(particle);
        }
        // need to find collisions
        // assuming this happens rather fast:
        int lastCount=0;
        int constantCount=0;
        while (constantCount < 1000)
        {
            // simulate step-wise (accelerate, then move)
            simulateStep(particles);
            // after each step, create a map of positions and particles
            // remove all particles which were hit
            removeCollisions(particles);
            // just wait until we have some stability (no new collisions)
            if (lastCount==particles.size())
            {
                constantCount++;
            }
            else
            {
                constantCount=0;
                lastCount=particles.size();
            }
        }
        return particles.size();
    }

    private void simulateStep(final List<Particle> particles)
    {
        for (Particle particle : particles)
        {
            particle.vx+=particle.ax;
            particle.vy+=particle.ay;
            particle.vz+=particle.az;
            particle.x+=particle.vx;
            particle.y+=particle.vy;
            particle.z+=particle.vz;
        }
    }

    private void removeCollisions(final List<Particle> particles)
    {
        List<Particle> toBeRemoved= new ArrayList<>();
        Map<String, Particle> positions = new HashMap<>();
        for (Particle particle : particles)
        {
            String key=particle.x+","+particle.y+","+particle.z;
            if (positions.containsKey(key))
            {
                toBeRemoved.add(particle);
                toBeRemoved.add(positions.get(key));
            }
            else
            {
                positions.put(key, particle);
            }
        }
        particles.removeAll(toBeRemoved);
    }

    private static class Particle
    {
        int num;
        long x,y,z;
        long vx, vy, vz;
        long ax, ay, az;

        Particle(final int i, String line)
        {
            num=i;
            List<Long> parts = AocParseUtils.getAllNumbersFromLine(line);
            x=parts.get(0);
            y=parts.get(1);
            z=parts.get(2);
            vx=parts.get(3);
            vy=parts.get(4);
            vz=parts.get(5);
            ax=parts.get(6);
            ay=parts.get(7);
            az=parts.get(8);
        }

        long accel()
        {
            return Math.abs(ax)+Math.abs(ay)+Math.abs(az);
        }
    }
}
