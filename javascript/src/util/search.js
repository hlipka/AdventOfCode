const { MaxPriorityQueue } = require('./maxPriorityQueue');

function bfs(world) {
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

function dfs(world) {
    let state = world.firstState();

    let states = new Set();
    function doSearch(world, state) {
        // when we reached our target, finish this branch
        // this method is supposed to also record the result somehow
        if (world.reachedTarget(state)) return;
        // when we have 'prune' logic, and can prune this state, we do so
        if (world.hasOwnProperty('canPrune') && world.canPrune(state)) return;
        // when we have logic to detect duplicate states, check whether we have seen the current state already
        if (world.hasOwnProperty('stateKey')) {
            let key = world.stateKey(state);
            if (states.has(key)) return;
            states.add(key);
        }
        // get the next set of states, and search through them
        let newStates = world.newStates(state);
        for (let s of newStates) {
            doSearch(world, s);
        }
    }

    doSearch(world, state);
}

exports.dfs=dfs;
exports.bfs=bfs;
