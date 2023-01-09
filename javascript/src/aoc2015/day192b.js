import {getLines} from "../util/parse.js";

let lines=getLines("2015", "19");
let molecule=lines.pop();
//let lines = ['H => HO', 'H => OH', 'O => HH', 'e => H', 'e => O' ]
//let molecule = 'HOHOHO';

// map the rules, and add the length
let rules = lines.map(l => l.split(" => "))
rules.forEach(l=>l.push(l[1].length-l[0].length))
// sort larger replacements first
rules.sort((a,b)=>b[2]-a[2])

console.log(rules);

let bestRounds=1000000;

// we replace backwards from the molecule until we find 'e'
// the first try will be the rules which strips the most of the string
// (not the longest one)
// this finds the first match quite fast (for my input), but takes a while to actually finish
// optimizations:
// - do _all_ replacements of a rule in one go, and only then jump to the next level (it seems there is regularity)
// - memoize string we find, to remove dulicates
function tryReplace(current, rounds) {
    if (current === 'e') {
        if (rounds<bestRounds) {
            bestRounds=rounds;
            console.log(bestRounds)
        }
        return;
    }
    for (let rule of rules) {
        let search = rule[1];
        let replace = rule[0];
        if (current.includes(search)) {
            let matches = current.matchAll(search);
            for (let m of matches) {
                let pre = current.slice(0, m.index);
                let post = current.slice(m.index + search.length);
                tryReplace(pre + replace + post, rounds+1);
            }
        }
    }
}

tryReplace(molecule, 0);

console.log(bestRounds);