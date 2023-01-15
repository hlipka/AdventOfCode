const {getLines} =require("../util/parse");

function setLight(light, from_row, from_col, to_row, to_col, value)
{
    for (let row=from_row; row<=to_row;row++) {
        for (let col=from_col; col<=to_col;col++) {
            lights[row*1000+col]=value;
        }
    }
}

function toggleLight(light, from_row, from_col, to_row, to_col)
{
    for (let row=from_row; row<=to_row;row++) {
        for (let col=from_col; col<=to_col;col++) {
            lights[row*1000+col]=!lights[row*1000+col];
        }
    }
}

function parse(line) {
    let parts=line.match(/([\S ]+) (\d+),(\d+) through (\d+),(\d+)/)
    return {'cmd': parts[1], 'from_row':Number(parts[2]), 'from_col':Number(parts[3]), 'to_row':Number(parts[4]), 'to_col':Number(parts[5])};
}
let commands=getLines("2015", "06").map(l=>parse(l));

let lights=new Array(1000000);
console.log(lights.length);
setLight(lights, 0, 999, 0, 999, false);
console.log(lights.length);

for (let cmd of commands ) {
    // normalize the command rectangle
    if (cmd.from_row>cmd.to_row) {
        let h=cmd.from_row;
        cmd.from_row=cmd.to_row;
        cmd.to_row=h;
    }
    if (cmd.from_col>cmd.to_col) {
        let h=cmd.from_col;
        cmd.from_col=cmd.to_col;
        cmd.to_col=h;
    }
    // execute the commands
    switch(cmd.cmd) {
        case 'turn on':
            setLight(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col, true)
            break;
        case 'turn off':
            setLight(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col, false)
            break;
        case 'toggle':
            toggleLight(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col)
            break;
        default: console.log("unknown command "+cmd.cmd);
    }
}
let count=0;
for (let l of lights) {
    if (l)
        count++;
}
console.log(lights.length);
console.log(count);

// 582499 is too high
// 583177 is too high
// 543903