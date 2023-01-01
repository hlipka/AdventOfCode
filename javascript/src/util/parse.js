import fs from "fs";

export function getFirstLine(year, day) {
    return fs.readFileSync('../../../data/'+year+'/day'+day+'.txt').toString().split("\n")[0];
}

export function getLines(year, day) {
    return fs.readFileSync('../../../data/'+year+'/day'+day+'.txt').toString().split("\n").filter(l => l.length > 0);
}
