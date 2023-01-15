const {getLines} =require("../util/parse");

function isNice(line) {
    let foundPair=false;
    for (let pos=0;pos<line.length-2;pos++)
    {
        // get the pair
        let pair=line.charAt(pos)+line.charAt(pos+1);
        // it must be found behind the pair occurrence
        if (line.includes(pair, pos+2)) {
            foundPair = true;
            break;
        }
    }
    if (!foundPair)
    {
        return false;
    }

    for (let pos=1;pos<line.length-1;pos++)
    {
        if (line.charAt(pos-1)===line.charAt(pos+1))
        {
            return true;
        }
    }
    return false;
}

console.log(isNice('qjhvhtzxzqqjkmpb'));
console.log(isNice('xxyxx'));
console.log(isNice('aaa'));
console.log(isNice('aaaa'));
console.log(isNice('uurcxstgmygtbstg'));
console.log(isNice('ieodomkazucvgmuy'));

const nice=getLines("2015", "05").filter(l=>isNice(l));

console.log(nice.length);
// 186 is too low
