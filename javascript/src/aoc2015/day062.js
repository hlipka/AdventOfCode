import {getLines} from '../util/parse.js';

function lightsOn(light, from_row, from_col, to_row, to_col)
{
    for (let row=from_row; row<=to_row;row++) {
        for (let col=from_col; col<=to_col;col++) {
            lights[row*1000+col]++;
        }
    }
}

function lightsOff(light, from_row, from_col, to_row, to_col)
{
    for (let row=from_row; row<=to_row;row++) {
        for (let col=from_col; col<=to_col;col++) {
            if (lights[row*1000+col]>0)
                lights[row*1000+col]--;
        }
    }
}

function lightsToggle(light, from_row, from_col, to_row, to_col)
{
    for (let row=from_row; row<=to_row;row++) {
        for (let col=from_col; col<=to_col;col++) {
            lights[row*1000+col]+=2;
        }
    }
}

function parse(line) {
    let parts=line.match(/([\S ]+) (\d+),(\d+) through (\d+),(\d+)/)
    return {'cmd': parts[1], 'from_row':Number(parts[2]), 'from_col':Number(parts[3]), 'to_row':Number(parts[4]), 'to_col':Number(parts[5])};
}
let commands=getLines("2015", "06").map(l=>parse(l));

let lights=new Array(1000000);
for (let row=0; row<=999;row++) {
    for (let col=0; col<=999;col++) {
        lights[row*1000+col]=0;
    }
}

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
            lightsOn(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col)
            break;
        case 'turn off':
            lightsOff(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col)
            break;
        case 'toggle':
            lightsToggle(lights, cmd.from_row, cmd.from_col, cmd.to_row, cmd.to_col)
            break;
        default: console.log("unknown command "+cmd.cmd);
    }
}
let count=0;
for (let row=0; row<=999;row++) {
    for (let col=0; col<=999;col++) {
        count+=lights[row*1000+col];
    }
}
console.log(lights.length);
console.log(count);

