const {getLines} =require("../util/parse");

let buckets=getLines("2015", "17").map(l=>Number(l)).sort((a,b)=>a-b);

const eggnog=150;
let count=0;
// we store the count per bucket-count in an array
let solutionCount=new Array(buckets.length);
solutionCount.fill(0);

function fit(currentBucket, currentSum, usedBuckets) {
    if (currentBucket===buckets.length) return; // we are done
    // when the fits with the current bucket, we have a solution
    if ((currentSum + buckets[currentBucket]) === eggnog)
    {
        count++;
        solutionCount[usedBuckets]++;
        // but still simulate with the next bucket in the list
        fit(currentBucket+1, currentSum, usedBuckets);
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
    fit(currentBucket+1, currentSum + buckets[currentBucket], usedBuckets+1);
    fit(currentBucket+1, currentSum, usedBuckets);
}

fit (0,0 , 0);
// 2804 is too low
console.log(count);
console.log(solutionCount);
