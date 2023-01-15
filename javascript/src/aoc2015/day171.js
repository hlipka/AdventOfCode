const {getLines} =require("../util/parse");

let buckets=getLines("2015", "17").map(l=>Number(l)).sort((a,b)=>a-b);

const eggnog=150;
let count=0;

function fit(currentBucket, currentSum) {
    if (currentBucket===buckets.length) return; // we are done
    // when the fits with the current bucket, we have a solution
    if ((currentSum + buckets[currentBucket]) === eggnog)
    {
        count++;
        // but still simulate with the next bucket in the list
        fit(currentBucket+1, currentSum);
        return;
    }
    // when the current bucket is too large for the remaining eggnog
    // we know that no other remaining buckets wil fit (they are sorted by size)
    // so we are done
    if ((eggnog-currentSum)<buckets[currentBucket])
    {
        return;
    }
    // check further down, with both the current bucket and without
    fit(currentBucket+1, currentSum + buckets[currentBucket]);
    fit(currentBucket+1, currentSum);
}

fit (0,0 );
// 2804 is too low
console.log(count);
