import {getLines} from "../util/parse.js";

let lines=getLines("2015", "19");
let molecule=lines.pop();
//let lines = ['H => HO', 'H => OH', 'O => HH']
//let molecule = 'HOH';
let rules = lines.map(l => l.split(" => "))

let maybe = new Set();

for (let r of rules) {
    let search = r[0];
    let matches = molecule.matchAll(search);
    for (let m of matches) {
        let pre = molecule.slice(0, m.index);
        let repl = r[1];
        let post = molecule.slice(m.index + search.length);
        let mb = pre + repl + post;
        maybe.add(mb);
    }
}

console.log(maybe.size);

