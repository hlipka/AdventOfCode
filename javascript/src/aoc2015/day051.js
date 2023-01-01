import {getLines} from '../util/parse.js';

function isNice(line) {
    if (line.includes('ab')||line.includes('cd')||line.includes('pq')||line.includes('xy'))
        return false;
    let vowels=(line.match(/[aeiou]/gi)||[]).length;
    if (vowels<3)
        return false;
    for (let pos=1;pos<line.length;pos++)
    {
        if (line.charAt(pos-1)===line.charAt(pos))
        {
            return true;
        }
    }
    return false;
}

console.log(isNice('ugknbfddgicrmopn'));
console.log(isNice('aaa'));
console.log(isNice('jchzalrnumimnmhp'));
console.log(isNice('haegwjzuvuyypxyu'));
console.log(isNice('dvszwmarrgswjxmb'));

const nice=getLines("2015", "05").filter(l=>isNice(l));

console.log(nice.length);
// 186 is too low
