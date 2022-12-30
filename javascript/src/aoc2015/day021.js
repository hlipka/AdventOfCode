var fs = require('fs');

function parseLine(line) {
    return line.match(/(\d+)x(\d+)x(\d+)/).slice(1, 4).map(n => Number(n)).sort((a, b) => a - b);
}

function getArea(parcel) {
    let slack = parcel[0] * parcel[1];
    let area = 2 * parcel[0] * parcel[1] + 2 * parcel[1] * parcel[2] + 2 * parcel[0] * parcel[2];
    return area + slack;
}

var array = fs.readFileSync('../../../data/2015/day02.txt').toString().split("\n");
let total = array.filter(l => l.length > 0).map(l => parseLine(l)).map(p => getArea(p)).reduce((s, a) => s + a);
console.log(total);

