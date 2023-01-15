const {getLines} = require('../util/parse');

const pieces=new Set(getLines('2015', "24").map(l=>Number(l)));

const comp=Array.from(pieces).reduce((a,b)=>a+b)/4;

let front=[];

// we track the min size so prune early
// there must be at least one set of this length
let minSetSize=Math.floor(pieces.size/4);

function getQE(e) {
    return Array.from(e).reduce((a,b)=>a*b,1);
}

function findFront(avail, set, current) {
    if (current===comp) {
        front.push(set);
        minSetSize=Math.min(minSetSize, set.size);
        return;
    }
    // no match, and we reached the max length, so we can prune
    if (set.size>=minSetSize) {
        return;
    }
    let from = Array.from(avail);
    from.sort((a,b)=>b-a);
    let nextAvail=new Set(avail); // we keep this set, so we don't check duplicated combinations
    for (let next of from) {
        // prune when the current total would be too large
        if ((current+next)>comp) {
            continue;
        }
        let nextSet=new Set(set);
        nextAvail.delete(next);
        nextSet.add(next);
        findFront(nextAvail, nextSet, current+next);
    }
}

// first, find all numbers which form the front compartment
// similar to part 1, we skip whether the remaining numbers form proper sets
findFront(pieces, new Set(), 0);

// order by length, filter out the ones with the wrong length
front.sort(e=>e.length);
const l=front[0].size;
const candidates=front.filter(e=>e.size===l).map(e=>[getQE(e), e]);

// now check them for their QE

candidates.sort((a,b)=>a[0]-b[0]);
console.log(candidates[0][0])

