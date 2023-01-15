const {getFirstLine} =require("../util/parse");

const line = getFirstLine("2015", "03");
console.log(line);
let houses = new Set();
let x = 0;
let y = 0;
let xr = 0;
let yr = 0;
let roboTurn = false;

houses.add('' + x + '-' + y);
for (const c of line) {
    if (roboTurn) {
        switch (c) {
            case '<':
                xr--;
                break;
            case '>':
                xr++;
                break;
            case 'v':
                yr--;
                break;
            case '^':
                yr++;
                break;
        }
        houses.add('' + xr + '-' + yr);
    }
    else {
        switch (c) {
            case '<':
                x--;
                break;
            case '>':
                x++;
                break;
            case 'v':
                y--;
                break;
            case '^':
                y++;
                break;
        }
        houses.add('' + x + '-' + y);
    }
    roboTurn = !roboTurn;
}
console.log(houses.size);

