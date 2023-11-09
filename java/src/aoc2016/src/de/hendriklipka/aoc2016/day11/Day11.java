package de.hendriklipka.aoc2016.day11;

import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Day11 {
    /*
        Test input:
        The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.
        The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
        The third floor contains a promethium generator, a promethium-compatible microchip, a ruthenium generator, and a ruthenium-compatible microchip.
        The fourth floor contains nothing relevant.
     */
    public static void main(String[] args) {
        State startState = new State();
        startState.floors[0].chips.add(new Chip("thulium"));
        startState.floors[0].generators.add(new Generator("thulium"));
        startState.floors[0].generators.add(new Generator("plutonium"));
        startState.floors[0].generators.add(new Generator("strontium"));
        startState.floors[1].chips.add(new Chip("plutonium"));
        startState.floors[1].chips.add(new Chip("strontium"));
        startState.floors[2].chips.add(new Chip("promethium"));
        startState.floors[2].chips.add(new Chip("ruthenium"));
        startState.floors[2].generators.add(new Generator("promethium"));
        startState.floors[2].generators.add(new Generator("ruthenium"));

        BuildingWorld world = new BuildingWorld(startState);

        DepthFirstSearch<BuildingWorld, State> search = new DepthFirstSearch<>(world);
    }

    private static class BuildingWorld implements SearchWorld<State>
    {
        int bestMoves = Integer.MAX_VALUE;
        private final State startState;

        private BuildingWorld(State startState) {
            this.startState = startState;
        }

        @Override
        public State getFirstState() {
            return startState;
        }

        @Override
        public List<State> calculateNextStates(State currentState) {
            //FIXME get next states
            return null;
        }

        @Override
        public boolean reachedTarget(State currentState) {
            return currentState.floors[0].isEmpty()&&currentState.floors[1].isEmpty()&&currentState.floors[2].isEmpty()&& currentState.elevator.floorNum==4;
        }

        @Override
        public boolean canPruneBranch(State currentState) {
            return currentState.moves>=bestMoves;
        }

        @Override
        public Comparator<State> getComparator() {
            return new Comparator<State>() {
                @Override
                public int compare(State o1, State o2) {
                    //FIXME implement
                    return 0;
                }
            };
        }
    }

    private static class State implements SearchState
    {
        int moves = 0;
        Floor[] floors = {new Floor(), new Floor(), new Floor(), new Floor()};
        Elevator elevator;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.equals(floors, state.floors) && Objects.equals(elevator, state.elevator);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(elevator);
            result = 31 * result + Arrays.hashCode(floors);
            return result;
        }

        @Override
        public String toString() {
            return "State{" +
                    "moves=" + moves +
                    ", floors=" + Arrays.toString(floors) +
                    ", elevator=" + elevator +
                    '}';
        }

        @Override
        public String calculateStateKey() {
            return "e="+elevator.toString()+ "f="+StringUtils.join(floors[0],"|");
        }
    }

    private static class Elevator
    {
        int floorNum=1;
        List<Chip> chips = new ArrayList<>();
        List<Generator> generators = new ArrayList<>();

        void addChip(Chip chip)
        {
            chips.add(chip);
        }

        void removeChip(Chip chip)
        {
            chips.remove(chip);
        }

        void addGenerator(Generator gen)
        {
            generators.add(gen);
        }

        void removeGenerator(Generator gen)
        {
            generators.remove(gen);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Elevator elevator = (Elevator) o;
            return floorNum == elevator.floorNum && Objects.equals(chips, elevator.chips) && Objects.equals(generators, elevator.generators);
        }

        @Override
        public int hashCode() {
            return Objects.hash(floorNum, chips, generators);
        }

        @Override
        public String toString() {
            return "Elev{" +
                    "f=" + floorNum +
                    ", c=" + chips +
                    ", g=" + generators +
                    '}';
        }
    }

    private static class Floor
    {
        List<Chip> chips = new ArrayList<>();
        List<Generator> generators = new ArrayList<>();

        void addChip(Chip chip)
        {
            chips.add(chip);
        }

        void removeChip(Chip chip)
        {
            chips.remove(chip);
        }

        void addGenerator(Generator gen)
        {
            generators.add(gen);
        }

        void removeGenerator(Generator gen)
        {
            generators.remove(gen);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Floor floor = (Floor) o;
            return Objects.equals(chips, floor.chips) && Objects.equals(generators, floor.generators);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chips, generators);
        }

        @Override
        public String toString() {
            return "F{" +
                    "c=" + chips +
                    ", g=" + generators +
                    '}';
        }

        public boolean isEmpty() {
            return chips.isEmpty()&& generators.isEmpty();
        }
    }

    private static class Chip
    {
        public String name;
        Chip(String name)
        {
            this.name=name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Chip chip = (Chip) o;
            return Objects.equals(name, chip.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "chip {"+ name + '}';
        }
    }

    private static class Generator
    {
        public String name;
        Generator(String name)
        {
            this.name=name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Generator generator = (Generator) o;
            return Objects.equals(name, generator.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "gen {"+ name + '}';
        }
    }
}
