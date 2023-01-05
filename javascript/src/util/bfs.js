import {
    MaxPriorityQueue,
} from './maxPriorityQueue';
export function dfs(world) {
    let statesSeen = new Set();
    // 'getScore' returns the 'goodness' value of each new state
    let statesToLookAt = new MaxPriorityQueue(world.getScore);

    statesToLookAt.push(world.firstState());

    while (statesToLookAt.size>0) {
        let state=statesToLookAt.pop();
        if (world.reachedTarget(state))
            continue;
        if (world.canPrune(state))
            continue;

        let key = world.stateKey(state);
        if (statesSeen.has(key))
            continue;
        statesSeen.add(key);

        world.newStates(state).forEach(s=>statesToLookAt.push(s));
    }
}