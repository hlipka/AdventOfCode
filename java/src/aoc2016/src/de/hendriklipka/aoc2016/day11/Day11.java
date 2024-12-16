package de.hendriklipka.aoc2016.day11;

import de.hendriklipka.aoc.search.BestFirstParallelSearch;
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
//        System.exit(1);
        time1 = System.currentTimeMillis();
        state = getStartState();
        moves = simulate(state);
        time2 = System.currentTimeMillis();
        System.out.println("result A: "+moves); // 41 is wrong
        System.out.println("took " + (time2 - time1) + " ms");
        time1 = System.currentTimeMillis();
        state = getStartState();
        state.floors[0].addChip("elerium");
        state.floors[0].addChip("dilithium");
        state.floors[0].addGenerator("elerium");
        state.floors[0].addGenerator("dilithium");
        moves = simulate(state);
        time2 = System.currentTimeMillis();
        System.out.println("result B: "+moves);
        System.out.println("took " + (time2 - time1) + " ms");
    }

    private static int simulate(final RTGState startState)
    {
        BuildingWorld world = new BuildingWorld(startState);

        BestFirstParallelSearch<BuildingWorld, RTGState> search = new BestFirstParallelSearch<>(world);
        search.search();
        return world.bestMoves;
    }

    private static RTGState getStartState()
    {
    /*
        actual input:
        The first floor contains a thulium String, a thulium-compatible microchip, a plutonium String, and a strontium String.
        The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
        The third floor contains a promethium String, a promethium-compatible microchip, a ruthenium String, and a ruthenium-compatible microchip.
        The fourth floor contains nothing relevant.
     */
        RTGState startState = new RTGState();
        startState.floors[0].addChip("thulium");
        startState.floors[0].addGenerator("thulium");
        startState.floors[0].addGenerator("plutonium");
        startState.floors[0].addGenerator("strontium");

        startState.floors[1].addChip("plutonium");
        startState.floors[1].addChip("strontium");

        startState.floors[2].addChip("promethium");
        startState.floors[2].addChip("ruthenium");
        startState.floors[2].addGenerator("promethium");
        startState.floors[2].addGenerator("ruthenium");
        return startState;
    }

    private static RTGState getTestState()
    {
    /*
        test input:
        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
        The second floor contains a hydrogen String.
        The third floor contains a lithium String.
        The fourth floor contains nothing relevant
     */
        RTGState startState = new RTGState();
        startState.floors[0].addChip("hydrogen");
        startState.floors[0].addChip("lithium");
        startState.floors[1].addGenerator("hydrogen");
        startState.floors[2].addGenerator("lithium");
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
            // pre-cache the state key so it is done in a separate thread instead of during the (synchronized) insert
            targetStates.forEach(RTGState::calculateStateKey);
            return targetStates;
        }

        private boolean isValid(final RTGState state)
        {
            final var elevator = state.elevator;
            // there must be stuff in the elevator
            if (elevator.chips.isEmpty() && elevator.generators.isEmpty())
                return false;
            final var currentFloor = state.floors[elevator.floorNum];
            for (String c: CollectionUtils.union(currentFloor.chips, elevator.chips))
            {
                // for each String, either the floor or the elevator must hold the corresponding String
                final var generators = CollectionUtils.union(currentFloor.generators, elevator.generators);
                if (!generators.isEmpty() && generators.stream().noneMatch(g->g.equals(c)))
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
                String c = currentState.elevator.chips.iterator().next();
                currentState.elevator.removeChip(c);
                currentFloor.addChip(c);
            }
            while (!currentState.elevator.generators.isEmpty())
            {
                String g = currentState.elevator.generators.iterator().next();
                currentState.elevator.removeGenerator(g);
                currentFloor.addGenerator(g);
            }
            // once the elevator is empty, we load stuff into it
            // since the stuff we had in the elevator is now on the floor, we will also have some states with all or some
            // of this stuff again in the elevator - this means we don't try to keep stuff in there, or keep track of what was there
            // loop over chips, take one

            // when we have multiple pairs on the current floor, only ever try to use the String or String from one pair (at least for the first chip)
            // using the other will not result in a faster solution

            // use this set to track the String combinations we already have seen
            Set<String> chipsDone=new HashSet<>();
            boolean chipPairSeen=false;
            for (String c: currentFloor.chips)
            {
                RTGState tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeChip(c);
                tempState.elevator.addChip(c);
                newStates.add(tempState); // we also try to use just one String
                if (tempState.floors[currentFloorNum].generators.contains(c))
                {
                    if (chipPairSeen)
                        continue;
                    chipPairSeen=true;
                }
                //   loop over the remaining chips, take one
                for (String c2: tempState.floors[currentFloorNum].chips)
                {
                    if (chipsDone.contains(c2 + "-" + c))
                        continue;
                    chipsDone.add(c + "-" + c2);
                    RTGState nextState=tempState.copyOf();
                    nextState.floors[currentFloorNum].removeChip(c2);
                    nextState.elevator.addChip(c2);
                    newStates.add(nextState);
                }
                //   loop over the generators. take one
                for (String g: tempState.floors[currentFloorNum].generators)
                {
                    RTGState nextState = tempState.copyOf();
                    nextState.floors[currentFloorNum].removeGenerator(g);
                    nextState.elevator.addGenerator(g);
                    newStates.add(nextState);
                }
            }
            // loop over the generators, take one
            Set<String> generatorsDone = new HashSet<>();
            chipPairSeen = false;
            for (String g : currentFloor.generators)
            {
                RTGState tempState = currentState.copyOf();
                tempState.floors[currentFloorNum].removeGenerator(g);
                tempState.elevator.addGenerator(g);
                newStates.add(tempState); // we also try just one String
                if (tempState.floors[currentFloorNum].chips.contains(g))
                {
                    if (chipPairSeen)
                        continue;
                    chipPairSeen = true;
                }
                //   loop the remaining generators, take one
                for (String g2 : tempState.floors[currentFloorNum].generators)
                {
                    if (generatorsDone.contains(g2 + "-" + g))
                        continue;
                    generatorsDone.add(g + "-" + g2);
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
        private int _score=Integer.MIN_VALUE;
        private String cachedKey=null;

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
            if (null!=cachedKey)
                return cachedKey;
            // create string of the form 'floor thing_type thing_name'
            // this includes the stuff from the elevator (using its current floor)
            // that way we have a canonical, unique list for the states
            // we also add the floor of the elevator at the end
            // any states with different pairs on the same floor are the same, essentially, so we report just the number of pairs
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < floors.length; i++)
            {
                Set<String> pairs = new HashSet<>();
                final Floor floor = floors[i];
                Set<String> chips = floor.chips;
                Set<String> generators = floor.generators;
                // when the elevator is on the current floor, we combine it into the floor data
                if (i== elevator.floorNum)
                {
                    chips = new HashSet<>(chips);
                    chips.addAll(elevator.chips);
                    generators = new HashSet<>(generators);
                    generators.addAll(elevator.generators);
                }
                for (String chip : chips)
                {
                    // when there is a generator for this chip, don't report the chip and add it to the list of pairs
                    if (generators.contains(chip))
                    {
                        pairs.add(chip);
                        continue;
                    }
                    keys.add((i+"c"+chip.substring(0,3)).intern());
                }
                for (String gen : generators)
                {
                    // when this is part of a pair, skip it
                    if (pairs.contains(gen))
                        continue;
                    keys.add((i+"g"+gen.substring(0,3)).intern());
                }
                // finally report the number of pairs
                if (!pairs.isEmpty())
                    keys.add((i + "p" + pairs.size()).intern());
            }
            keys.add("e"+elevator.floorNum);
            keys.sort(String::compareTo);
            cachedKey = StringUtils.join(keys, ",");
            return cachedKey;
        }

        public int stateScore()
        {
            if (_score==Integer.MIN_VALUE)
                _score= -(floors[0].chips.size() + floors[0].generators.size() +
                          3 * (floors[1].chips.size() + floors[1].generators.size()) +
                          7 * (floors[2].chips.size() + floors[2].generators.size()) +
                          13 * (floors[3].chips.size() + floors[3].generators.size()) +
                          17 * elevator.floorNum
                          - 1011 * moves);
            return _score;
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
        Set<String> chips = new HashSet<>();
        Set<String> generators = new HashSet<>();

        void addChip(String String)
        {
            chips.add(String);
        }

        void removeChip(String String)
        {
            chips.remove(String);
        }

        void addGenerator(String gen)
        {
            generators.add(gen);
        }

        void removeGenerator(String gen)
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
        Set<String> chips = new HashSet<>();
        Set<String> generators = new HashSet<>();

        void addChip(String String)
        {
            chips.add(String);
        }

        void removeChip(String String)
        {
            chips.remove(String);
        }

        void addGenerator(String gen)
        {
            generators.add(gen);
        }

        void removeGenerator(String gen)
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
}
