import {getLines} from '../util/parse.js';

function parse(line) {
    const parts = line.match(/(\S+) can fly (\d+) km\/s for (\d+) seconds, but then must rest for (\d+) seconds./);
    return {
        name: parts[1],
        speed: Number(parts[2]),
        fly: Number(parts[3]),
        rest: Number(parts[4]),
        dist: 0,
        flyTime: 0,
        restTime: 0,
        flying: true,
        score: 0
    };
}

let reindeer=getLines("2015", "14").map(l=>parse(l));


for (let i=0;i<2503;i++) {
    reindeer.forEach(d=>race(d));
    reindeer.sort((a,b)=>a.dist-b.dist).reverse();
    let maxDist=reindeer[0].dist;
    reindeer.filter(d=>d.dist===maxDist).forEach(d=>d.score++);

}
function race(deer) {
    if (deer.flying) {
        deer.flyTime++;
        deer.dist+=deer.speed;
        if (deer.flyTime>=deer.fly) {
            deer.flying=false;
            deer.flyTime=0;
        }
    }
    else {
        deer.restTime++;
        if (deer.restTime>=deer.rest) {
            deer.flying=true;
            deer.restTime=0;
        }
    }
}
// 1044 is too low
reindeer.sort((a,b)=>a.score-b.score).reverse();
console.log(reindeer);
