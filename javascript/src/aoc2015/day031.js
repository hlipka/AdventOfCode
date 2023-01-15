const {getFirstLine} =require("../util/parse");

const line=getFirstLine("2015", "03");
console.log(line);
let houses = new Set();
let x=0;
let y=0;

houses.add(''+x+'-'+y);
for (const c of line) {
    switch (c)
    {
        case '<': x--; break;
        case '>': x++; break;
        case 'v': y--; break;
        case '^': y++; break;
    }
    houses.add(''+x+'-'+y);
}
console.log(houses.size);

