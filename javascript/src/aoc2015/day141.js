const {getLines} =require("../util/parse");

function parse(line) {
    const parts = line.match(/(\S+) can fly (\d+) km\/s for (\d+) seconds, but then must rest for (\d+) seconds./);
    return {
        name: parts[1],
        speed: Number(parts[2]),
        fly: Number(parts[3]),
        rest: Number(parts[4]),
    };
}

let reindeer=getLines("2015", "14").map(l=>parse(l));

function race(deer, time) {
    const period=deer.fly+deer.rest;
    const periods=Math.floor(time/period);
    const inPeriod=time%period;
    let dist = periods*deer.speed*deer.fly;
    if (inPeriod>=deer.fly) {
        dist+=deer.speed*deer.fly;
    }
    else
    {
        dist+=inPeriod*deer.speed;
    }
    return dist;
}

let distances=reindeer.map(d=>race(d, 2503)).sort().reverse();
console.log(distances[0]);