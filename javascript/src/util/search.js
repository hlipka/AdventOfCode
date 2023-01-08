import {
    MaxPriorityQueue,
} from './maxPriorityQueue';
export function bfs(world) {
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

export function dfs(world) {
    let state = world.firstState();

    let states = new Set();
    function doSearch(world, state) {
        if (world.reachedTarget(state)) return;
        if (world.hasOwnProperty('canPrune') && world.canPrune(state)) return;
        if (world.hasOwnProperty('stateKey')) {
            let key = world.stateKey(state);
            if (states.has(key)) return;
            states.add(key);
        }
        let newStates = world.newStates(state);
        for (let s of newStates) {
            doSearch(world, s);
        }
    }

    doSearch(world, state);
}