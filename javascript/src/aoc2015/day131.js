import {getLines} from '../util/parse.js';
const { dfs } = require('../util/search');

let happiness = new Map();
let guests = new Set();

function calculateHappiness(table) {
    let happy=happiness.get(table[table.length-1]+'-'+table[0])+happiness.get(table[0]+'-'+table[table.length-1]);
    for (let i=0;i<table.length-1;i++) {
        happy+=happiness.get(table[i]+'-'+table[i+1]);
        happy+=happiness.get(table[i+1]+'-'+table[i]);
    }
    return happy;
}

let world = {
    count: 0,
    best: 0,
    guests: null,

    firstState() {
        return {
            table: [],
            remaining: guests
        };
    },

    reachedTarget(state) {
        if (state.table.length === this.count) {
            let happy=calculateHappiness(state.table);
            if (happy>this.best) {
                console.log("new best table", state.table);
                console.log("happiness", happy);
                this.best=happy;
            }
            return true;
        }
        return false;
    },

    newStates(state) {
        let states = [];
        for (let guest of state.remaining) {
            let newState = {
                table: Array.from(state.table),
                remaining: new Set(state.remaining)
            };
            newState.table.push(guest);
            newState.remaining.delete(guest);
            states.push(newState);
        }
        return states;
    }
}

function parse(line) {
    let parts=line.match(/(\S+) would (\S+) (\d+) happiness units by sitting next to (\S+)./);
    return {
        from: parts[1],
        to: parts[4],
        gain: (parts[2]==='gain')?Number(parts[3]):-Number(parts[3])
    };
}


getLines("2015","13").map(l=>parse(l)).forEach(h=>{happiness.set(h.from+'-'+h.to,h.gain); guests.add(h.from)});
console.log(happiness)
console.log(guests);

world.count=guests.size;

dfs(world);

console.log(world.best);