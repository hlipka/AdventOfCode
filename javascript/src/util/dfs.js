
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