import {getLines} from '../util/parse.js';

let facts = new Set([
"children: 3",
"cats: 7",
"samoyeds: 2",
"pomeranians: 3",
"akitas: 0",
"vizslas: 0",
"goldfish: 5",
"trees: 3",
"cars: 2",
"perfumes: 1"
])

function getFacts(line) {
    let parts=line.split(/: (.*)/s);

    return {
        aunt: parts[0],
        facts: new Set(parts[1].split(', '))
    };
}

// parse the aunts, and store their facts
let aunts=getLines("2015", "16").map(l=>getFacts(l));

// for each aunt, we remove the knows facts from their facts
// when we have an aunt with empty facts afterwards, it's the right one
for (let aunt of aunts) {
    let afacts=aunt.facts;
    for (let fact of facts)
        afacts.delete(fact);
    if (afacts.size===0) {
        console.log(aunt.aunt)
    }
}
