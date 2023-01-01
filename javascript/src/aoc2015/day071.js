import {getLines} from '../util/parse.js';

function transform2(cmd, left, right) {
    switch(cmd) {
        case 'AND': return (left & right);
        case 'OR': return (left | right);
        case 'LSHIFT': return (left << right);
        case 'RSHIFT': return (left >> right);
    }
    console.log('unknown command  '+cmd);
}

function transform1(cmd, input) {
    if (cmd==='NOT') {
        return ~input;
    }
    else {
        console.log('unknown command  '+cmd);
    }
}

function parseValue(value) {
    return isNaN(value) ? value : Number(value);
}

const setExpr=/^(\d+) -> (\S+)/;
const twoOpExpr=/^(\S+) (\S+) (\S+) -> (\S+)/;
const oneOpExpr=/^(\S+) (\S+) -> (\S+)/;
const assgnExpr=/^(\S+) -> (\S+)/;

function parse(line) {
    if (twoOpExpr.test(line))
    {
        const parts=line.match(twoOpExpr);
        return {'op': parts[2], 'left':parseValue(parts[1]), 'right':parseValue(parts[3]), 'wire':parts[4]}
    }
    if (oneOpExpr.test(line))
    {
        const parts=line.match(oneOpExpr);
        return {'op': parts[1], 'in':parseValue(parts[2]), 'wire':parts[3]}
    }
    // no number values here, these would haven been handled directly
    if (assgnExpr.test(line))
    {
        const parts=line.match(assgnExpr);
        return {'in':parts[1], 'wire':parts[2]}
    }
    console.log("unknown operation: "+line)
}

function runCommand(cmd, wires) {
    if (cmd.left) { // two-op command
        let leftIsNum = typeof (cmd.left) === 'number';
        let rightIsNum = typeof (cmd.right) === 'number';
        let leftExists = wires.has(cmd.left) || leftIsNum;
        let rightExists = wires.has(cmd.right) || rightIsNum;
        if (leftExists && rightExists)
        {
            wires.set(cmd.wire, transform2(cmd.op, wires.get(cmd.left) || cmd.left, wires.get(cmd.right) || cmd.right));
            return true;
        }

    }
    else if (cmd.op) { // one-op command
        if (typeof(cmd.in)==='number' || wires.has(cmd.in))
        {
            wires.set(cmd.wire, transform1(cmd.op, wires.get(cmd.in) || cmd.in));
            return true;
        }
    }
    else { // assignment
        if (wires.has(cmd.in))
        {
            wires.set(cmd.wire, wires.get(cmd.in));
            return true;
        }
    }
    return false;
}

let commands=getLines("2015","07");
// initial assignments
let sets=commands.filter(l=>setExpr.test(l));
let ops=commands.filter(l=>!setExpr.test(l)).map(l=>parse(l));

let wires=new Map();
for (let set of sets) {
    const parts=set.match(setExpr);
    const wire=parts[2];
    wires.set(wire, Number(parts[1]));
}

while (!wires.has('a')) {
    let runOne=false;
    for (let idx=0;idx<ops.length;idx++)
    {
        let cmd=ops[idx];
        if (cmd === undefined)
            continue;
        if (runCommand(cmd, wires)) {
            delete ops[idx];
            runOne=true;
        }
    }
    if (!runOne) {
        console.log("could not run a command anymore");
        break;
    }
}
console.log(wires.get("a"));

