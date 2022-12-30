var fs = require('fs');

function parseLine(line) {
    return line.match(/(\d+)x(\d+)x(\d+)/).slice(1, 4).map(n => Number(n)).sort((a, b) => a - b);
}

function getRibbon(parcel) {
    let bow = parcel[0] * parcel[1] * parcel[2];
    let ribbon = parcel[0] + parcel[0] + parcel[1] + parcel[1];
    return bow + ribbon;
}

var array = fs.readFileSync('../../../data/2015/day02.txt').toString().split("\n");
let total = array.filter(l => l.length > 0).map(l => parseLine(l)).map(p => getRibbon(p)).reduce((s, a) => s + a);
console.log(total);

