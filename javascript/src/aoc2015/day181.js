const {getAsCharMatrix} =require("../util/parse");

let world=getAsCharMatrix("2015", "18",".");

const width=world[0].length-2;
const height=world.length-2;

function neighbour(row, col, world) {
    return world[row][col]==='#'?1:0;
}
function simulate(world) {
    let newWorld=[];

    // first, set up an empty new world
    for (let row=0;row<height+2; row++) {
        let a=new Array(width+2);
        a.fill('.');
        newWorld.push(a);
    }

    for (let row=1;row<=height; row++) {
        for (let col=1;col<=width;col++) {
            let n = 0;
            n += neighbour(row - 1, col - 1, world);
            n += neighbour(row - 1, col, world);
            n += neighbour(row - 1, col + 1, world);
            n += neighbour(row, col - 1, world);
            n += neighbour(row + 1, col - 1, world);
            n += neighbour(row + 1, col, world);
            n += neighbour(row + 1, col + 1, world);
            n += neighbour(row, col + 1, world);
            if (world[row][col] === '#') {
                if (n === 2 || n === 3) {
                    newWorld[row][col] = '#';
                } else {
                    newWorld[row][col] = '.';
                }
            }
            else if (n === 3) {
                newWorld[row][col] = '#';
            } else {
                newWorld[row][col] = '.';
            }
        }
    }

    return newWorld;
}

for (let i=0;i<100;i++) {
    world=simulate(world);
}

let lights=0;
for (let row=1;row<=height;row++)
    for (let col=1;col<=width;col++)
        if ('#'===world[row][col]) lights++;

console.log(lights);

