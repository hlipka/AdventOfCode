package de.hendriklipka.aoc2016.day11;

import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Day11 {
    public static void main(String[] args) {
        long time1=System.currentTimeMillis();
        var state = getTestState();
        var moves = simulate(state);
        long time2=System.currentTimeMillis();
        System.out.println("result example: "+moves);
        System.out.println("took "+(time2-time1)+" ms");
        time1 = System.currentTimeMillis();
        state = getStartState();
        moves = simulate(state);
        time2 = System.currentTimeMillis();
        System.out.println("result A: "+moves); // 41 is wrong
        System.out.println("took " + (time2 - time1) + " ms");
        time1 = System.currentTimeMillis();
        state = getStartState();
        state.floors[0].addChip(new Chip("elerium"));
        state.floors[0].addChip(new Chip("dilithium"));
        state.floors[0].addGenerator(new Generator("elerium"));
        state.floors[0].addGenerator(new Generator("dilithium"));
        moves = simulate(state);
        time2 = System.currentTimeMillis();
        System.out.println("result B: "+moves);
        System.out.println("took " + (time2 - time1) + " ms");
    }

    private static int simulate(final RTGState startState)
    {
        BuildingWorld world = new BuildingWorld(startState);

        BestFirstSearch<BuildingWorld, RTGState> search = new BestFirstSearch<>(world);
        search.search();
        return world.bestMoves;
    }

    private static RTGState getStartState()
    {
    /*
        actual input:
        The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.
        The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
        The third floor contains a promethium generator, a promethium-compatible microchip, a ruthenium generator, and a ruthenium-compatible microchip.
        The fourth floor contains nothing relevant.
     */
        RTGState startState = new RTGState();
        startState.floors[0].addChip(new Chip("thulium"));
        startState.floors[0].addGenerator(new Generator("thulium"));
        startState.floors[0].addGenerator(new Generator("plutonium"));
        startState.floors[0].addGenerator(new Generator("strontium"));

        startState.floors[1].addChip(new Chip("plutonium"));
        startState.floors[1].addChip(new Chip("strontium"));

        startState.floors[2].addChip(new Chip("promethium"));
        startState.floors[2].addChip(new Chip("ruthenium"));
        startState.floors[2].addGenerator(new Generator("promethium"));
        startState.floors[2].addGenerator(new Generator("ruthenium"));
        return startState;
    }

    private static RTGState getTestState()
    {
    /*
        test input:
        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
        The second floor contains a hydrogen generator.
        The third floor contains a lithium generator.
        The fourth floor contains nothing relevant
     */
        RTGState startState = new RTGState();
        startState.floors[0].addChip(new Chip("hydrogen"));
        startState.floors[0].addChip(new Chip("lithium"));
        startState.floors[1].addGenerator(new Generator("hydrogen"));
        startState.floors[2].addGenerator(new Generator("lithium"));
        return startState;
    }

    private static class BuildingWorld implements SearchWorld<RTGState>
    {
        int bestMoves = Integer.MAX_VALUE;
        private final RTGState startState;

        private BuildingWorld(RTGState startState) {
            this.startState = startState;
        }

        @Override
        public RTGState getFirstState() {
            return startState;
        }

        @Override
        public List<RTGState> calculateNextStates(RTGState currentState) {
            final List<RTGState> targetStates = new ArrayList<>();
            // get all variants of loading and unloading the elevator
            List<RTGState> elevators = loadAndUnload(currentState);
            for (RTGState state: elevators)
            {
                // try to go up and down
                // the new state must be valid, and we must not have seen it before
                if (state.elevator.floorNum < 3)
                {
                    RTGState target = state.copyOf();
                    target.elevator.floorNum++;
                    target.moves = currentState.moves + 1;
                    if (isValid(target))
                         targetStates.add(target);
                }
                if (state.elevator.floorNum > 0)
                {
                    RTGState target = state.copyOf();
                    target.elevator.floorNum--;
                    target.moves = currentState.moves+1;
                    if (isValid(target))
                        targetStates.add(target);
                }
            }
            return targetStates;
        }

        private boolean isValid(final RTGState state)
        {
            final var elevator = state.elevator;
            // there must be stuff in the elevator
            if (elevator.chips.isEmpty() && elevator.generators.isEmpty())
                return false;
            final var currentFloor = state.floors[elevator.floorNum];
            for (Chip c: CollectionUtils.union(currentFloor.chips, elevator.chips))
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

        private List<RTGState> loadAndUnload(final RTGState currentState)
        {
            final var currentFloorNum = currentState.elevator.floorNum;
            final var currentFloor = currentState.floors[currentFloorNum];
            final List<RTGState> newStates = new ArrayList<>();
            // first, unload the elevator
            while (!currentState.elevator.chips.isEmpty())
            {
                Chip c = currentState.elevator.chips.iterator().next();
                currentState.elevator.removeChip(c);
                currentFloor.addChip(c);
            }
            while (!currentState.elevator.generators.isEmpty())
            {
                Generator g = currentState.elevator.generators.iterator().next();
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
                RTGState tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeChip(c);
                tempState.elevator.addChip(c);
                newStates.add(tempState); // we also try to use just one chip
                //   loop over the remaining chips, take one
                for (Chip c2: tempState.floors[currentFloorNum].chips)
                {
                    if (chipsDone.contains(c2.name + "-" + c.name))
                        continue;
                    chipsDone.add(c.name + "-" + c2.name);
                    RTGState nextState=tempState.copyOf();
                    nextState.floors[currentFloorNum].removeChip(c2);
                    nextState.elevator.addChip(c2);
                    newStates.add(nextState);
                }
                //   loop over the generators. take one
                for (Generator g: tempState.floors[currentFloorNum].generators)
                {
                    RTGState nextState = tempState.copyOf();
                    nextState.floors[currentFloorNum].removeGenerator(g);
                    nextState.elevator.addGenerator(g);
                    newStates.add(nextState);
                }
            }
            // loop over the generators, take one
            Set<String> generatorsDone = new HashSet<>();
            for (Generator g : currentFloor.generators)
            {
                RTGState tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeGenerator(g);
                tempState.elevator.addGenerator(g);
                newStates.add(tempState); // we also try just one generator
                //   loop the remaining generators, take one
                for (Generator g2 : tempState.floors[currentFloorNum].generators)
                {
                    if (generatorsDone.contains(g2.name + "-" + g.name))
                        continue;
                    generatorsDone.add(g.name + "-" + g2.name);
                    RTGState nextState = tempState.copyOf();
                    nextState.floors[currentFloorNum].removeGenerator(g2);
                    nextState.elevator.addGenerator(g2);
                    newStates.add(nextState);
                }
            }
            return newStates;
        }

        @Override
        public boolean reachedTarget(RTGState currentState) {
            final var finished = currentState.floors[0].isEmpty() && currentState.floors[1].isEmpty() && currentState.floors[2].isEmpty() && currentState.elevator.floorNum == 3;
            if (finished)
            {
                if (currentState.moves<bestMoves)
                {
                    bestMoves=currentState.moves;
                }
            }
            return finished;
        }

        @Override
        public boolean canPruneBranch(RTGState currentState) {
            return currentState.moves>=bestMoves;
        }

        @Override
        public Comparator<RTGState> getComparator() {
            return Comparator.comparingInt(RTGState::stateScore);
        }
    }

    private static class RTGState implements SearchState
    {
        int moves = 0;
        Floor[] floors = {new Floor(), new Floor(), new Floor(), new Floor()};
        Elevator elevator= new Elevator();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RTGState state = (RTGState) o;
            return calculateStateKey().equals(state.calculateStateKey());
        }

        @Override
        public boolean betterThan(final Object other)
        {
            return moves<(Integer)other;
        }

        @Override
        public Object getCurrentCost()
        {
            return moves;
        }

        @Override
        public int hashCode() {
            return calculateStateKey().hashCode();
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
            // create string of the form 'floor thing_type thing_name'
            // this includes the stuff from the elevator (using its current floor)
            // that way we have a canonical, unique list for the states
            // we also add the floor of the elevator at the end
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < floors.length; i++)
            {
                final Floor floor = floors[i];
                for (Chip chip : floor.chips)
                {
                    keys.add((i+"c"+chip.name.substring(0,3)).intern());
                }
                for (Generator gen : floor.generators)
                {
                    keys.add((i+"g"+gen.name.substring(0,3)).intern());
                }
            }
            for (Chip chip : elevator.chips)
            {
                keys.add((elevator.floorNum+"c"+chip.name.substring(0,3)).intern());
            }
            for (Generator gen : elevator.generators)
            {
                keys.add((elevator.floorNum+"g"+gen.name.substring(0,3)).intern());
            }
            keys.add("e"+elevator.floorNum);
            keys.sort(String::compareTo);
            return StringUtils.join(keys,",");
        }

        public int stateScore()
        {
            // a lower number means a preferred solution
            // we want to prefer a solution with more things on higher floors
            return
             -(floors[0].chips.size()+ floors[0].generators.size()+
                   3*(floors[1].chips.size() + floors[1].generators.size())+
                   7*(floors[2].chips.size() + floors[2].generators.size())+
                   13*(floors[3].chips.size() + floors[3].generators.size())+
                   17*elevator.floorNum);
        }

        public RTGState copyOf()
        {
            final RTGState state = new RTGState();
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
        Set<Chip> chips = new HashSet<>();
        Set<Generator> generators = new HashSet<>();

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
            elevator.chips.addAll(chips);
            elevator.generators.addAll(generators);
            return elevator;
        }
    }

    private static class Floor
    {
        Set<Chip> chips = new HashSet<>();
        Set<Generator> generators = new HashSet<>();

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
            floor.chips.addAll(chips);
            floor.generators.addAll(generators);
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
