package de.hendriklipka.aoc2016.day11;

import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Day11 {
    public static void main(String[] args) {
        System.out.println("building example");
        var state = getTestState();
        System.out.println("searching example");
        var moves = simulate(state);
        System.out.println("example moves: "+moves);
        state = getStartState();
        moves = simulate(state);
        System.out.println("result: "+moves);
    }

    private static int simulate(final State startState)
    {
        BuildingWorld world = new BuildingWorld(startState);

        BestFirstSearch<BuildingWorld, State> search = new BestFirstSearch<>(world);
        search.search();
        return world.bestMoves;
    }

    private static State getStartState()
    {
    /*
        actual input:
        The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.
        The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
        The third floor contains a promethium generator, a promethium-compatible microchip, a ruthenium generator, and a ruthenium-compatible microchip.
        The fourth floor contains nothing relevant.
     */
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
        return startState;
    }

    private static State getTestState()
    {
    /*
        test input:
        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
        The second floor contains a hydrogen generator.
        The third floor contains a lithium generator.
        The fourth floor contains nothing relevant
     */
        State startState = new State();
        startState.floors[0].chips.add(new Chip("hydrogen"));
        startState.floors[0].chips.add(new Chip("lithium"));
        startState.floors[1].generators.add(new Generator("hydrogen"));
        startState.floors[2].generators.add(new Generator("lithium"));
        return startState;
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
            final List<State> targetStates = new ArrayList<>();
            // get all variants of loading and unloading the elevator
            List<State> elevators = loadAndUnload(currentState);
            for (State state: elevators)
            {
                // try to go up and down
                // the new state must be valid, and we must not have seen it before
                if (state.elevator.floorNum > 0)
                {
                    State target = state.copyOf();
                    target.elevator.floorNum--;
                    target.moves = currentState.moves+1;
                     if (isValid(target))
                         targetStates.add(target);
                }
                if (state.elevator.floorNum < 3)
                {
                    State target = state.copyOf();
                    target.elevator.floorNum++;
                    target.moves = currentState.moves + 1;
                    if (isValid(target))
                         targetStates.add(target);
                }
            }
            return targetStates;
        }

        private boolean isValid(final State state)
        {
            final var elevator = state.elevator;
            // there must be stuff in the elevator
            if (elevator.chips.isEmpty() && elevator.generators.isEmpty())
                return false;
            final var currentFloor = state.floors[elevator.floorNum];
            final List<Chip> floorChips = currentFloor.chips;
            for (Chip c: CollectionUtils.union(floorChips, elevator.chips))
            {
                String cName=c.name;
                // for each chip, either the floor or the elevator must hold the corresponding generator
                final var generators = CollectionUtils.union(currentFloor.generators, elevator.generators);
                if (!generators.isEmpty() && generators.stream().noneMatch(g->g.name.equals(cName)))
                    return false;
            }
            // we do not need to check the elevator contents - when it would be unsafe it would already be caught at the previous floor, or at the new one
            return true;
        }

        private List<State> loadAndUnload(final State currentState)
        {
            final var currentFloorNum = currentState.elevator.floorNum;
            final var currentFloor = currentState.floors[currentFloorNum];
            final List<State> newStates = new ArrayList<>();
            // first, unload the elevator
            while (!currentState.elevator.chips.isEmpty())
            {
                Chip c = currentState.elevator.chips.get(0);
                currentState.elevator.removeChip(c);
                currentFloor.addChip(c);
            }
            while (!currentState.elevator.generators.isEmpty())
            {
                Generator g = currentState.elevator.generators.get(0);
                currentState.elevator.removeGenerator(g);
                currentFloor.addGenerator(g);
            }
            // once the elevator is empty, we load stuff into it
            // since the stuff we had in the elevator is now on the floor, we will also have some states with all or some
            // of this stuff again in the elevator - this means we don't try to keep stuff in there, or keep track of what was there
            // loop over chips, take one

            // use this set to track the chip combinations we already have seen
            Set<String> chipsDone=new HashSet<>();
            for (Chip c: currentFloor.chips)
            {
                State tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeChip(c);
                tempState.elevator.addChip(c);
                newStates.add(tempState); // we also try to use just one chip
                //   loop over the remaining chips, take one
                for (Chip c2: tempState.floors[currentFloorNum].chips)
                {
                    if (chipsDone.contains(c2.name + "-" + c.name))
                        continue;
                    chipsDone.add(c.name + "-" + c2.name);
                    State nextState=tempState.copyOf();
                    nextState.floors[currentFloorNum].removeChip(c2);
                    nextState.elevator.addChip(c2);
                    newStates.add(nextState);
                }
                //   loop over the generators. take one
                for (Generator g: tempState.floors[currentFloorNum].generators)
                {
                    State nextState = tempState.copyOf();
                    nextState.floors[currentFloorNum].removeGenerator(g);
                    nextState.elevator.addGenerator(g);
                    newStates.add(nextState);
                }
            }
            // loop over the generators, take one
            Set<String> generatorsDone = new HashSet<>();
            for (Generator g : currentFloor.generators)
            {
                State tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeGenerator(g);
                tempState.elevator.addGenerator(g);
                newStates.add(tempState); // we also try just one generator
                //   loop the remaining generators, take one
                for (Generator g2 : tempState.floors[currentFloorNum].generators)
                {
                    if (generatorsDone.contains(g2.name + "-" + g.name))
                        continue;
                    generatorsDone.add(g.name + "-" + g2.name);
                    State nextState = tempState.copyOf();
                    nextState.floors[currentFloorNum].removeGenerator(g2);
                    nextState.elevator.addGenerator(g2);
                    newStates.add(nextState);
                }
            }
            return newStates;
        }

        @Override
        public boolean reachedTarget(State currentState) {
            if (currentState.moves>10000)
                return true;
            final var finished = currentState.floors[0].isEmpty() && currentState.floors[1].isEmpty() && currentState.floors[2].isEmpty() && currentState.elevator.floorNum == 3;
            if (finished)
                bestMoves=currentState.moves;
            return finished;
        }

        @Override
        public boolean canPruneBranch(State currentState) {
            return currentState.moves>=bestMoves;
        }

        @Override
        public Comparator<State> getComparator() {
            return Comparator.comparingInt(State::stateScore);
        }
    }

    private static class State implements SearchState
    {
        int moves = 0;
        Floor[] floors = {new Floor(), new Floor(), new Floor(), new Floor()};
        Elevator elevator= new Elevator();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.equals(floors, state.floors) && Objects.equals(elevator, state.elevator);
        }

        @Override
        public boolean betterThan(final SearchState other)
        {
            return moves<((State)other).moves;
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
            // FIXME - create a union of floor+elevator, sort everything in there and use that one
            // also, use a short representation with just the IDs and nothing else
            return "e="+elevator.toString()+ "f="+StringUtils.join(floors,"|");
        }

        public int stateScore()
        {
            // higher score means we have more stuff at the top floor
            // otherwise, prefer as higher elevator
            return floors[0].chips.size()+ floors[0].generators.size()+
                   3*(floors[0].chips.size() + floors[0].generators.size())+
                   7*(floors[0].chips.size() + floors[0].generators.size())+
                   13*(floors[0].chips.size() + floors[0].generators.size())+
                   17*elevator.floorNum;
        }

        public State copyOf()
        {
            final State state = new State();
            state.floors = new Floor[floors.length];
            for (int i = 0; i < floors.length; i++)
            {
                state.floors[i]=floors[i].copyOf();
            }
            state.elevator = elevator.copyOf();
            state.moves = moves;
            return state;
        }
    }

    private static class Elevator
    {
        int floorNum=0;
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

        public Elevator copyOf()
        {
            final Elevator elevator = new Elevator();
            elevator.floorNum = floorNum;
            elevator.chips = new ArrayList<>(chips);
            elevator.generators = new ArrayList<>(generators);
            return elevator;
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

        public Floor copyOf()
        {
            final Floor floor=new Floor();
            floor.chips = new ArrayList<>(chips);
            floor.generators = new ArrayList<>(generators);
            return floor;
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
