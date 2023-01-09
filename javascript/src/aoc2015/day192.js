import {getLines} from "../util/parse.js";

let lines=getLines("2015", "19");
let molecule=lines.pop();
//let lines = ['H => HO', 'H => OH', 'O => HH', 'e => H', 'e => O' ]
//let molecule = 'HOHOHO';

const maxLen=molecule.length;

// map the rules, and add the length
let rules = lines.map(l => l.split(" => "))
rules.forEach(l=>l.push(l[1].length-l[0].length))
// sort larger replacements first
rules.sort((a,b)=>b[2]-a[2])

console.log(rules);

let bestRounds=1000000;

// do a forward replace
// NOTE: this is way too slow
function tryReplace(e, rounds) {
    if (e === molecule) {
        if (rounds<bestRounds) {
            bestRounds=rounds;
            console.log(bestRounds)
        }
        return;
    }
    for (let rule of rules) {
        // when the current replacement would be too long, we will not consider it (strings cannot be shortened)
        if (e.length+rule[2]>maxLen) return;
        let search = rule[0];
        if (e.includes(search)) {
            let matches = e.matchAll(search);
            for (let m of matches) {
                let pre = e.slice(0, m.index);
                let post = e.slice(m.index + search.length);
                tryReplace(pre + rule[1] + post, rounds+1);
            }
        }
    }
}

tryReplace('e', 0);

console.log(bestRounds);