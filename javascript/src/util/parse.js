const fs = require("fs");

function getFirstLine(year, day) {
    return fs.readFileSync('../../../data/'+year+'/day'+day+'.txt').toString().split("\n")[0];
}

function getLines(year, day) {
    return fs.readFileSync('../../../data/'+year+'/day'+day+'.txt').toString().split("\n").filter(l => l.length > 0);
}

function getAsCharMatrix(year, day, border=null) {
    let lines=getLines(year, day);
    const width=lines.map(l=>l.length).reduce((a,b)=>Math.max(a,b));
    if (border) {
        let result=[];
        let a=new Array(width+2);
        a.fill(border);
        result.push(a);
        for (let line of lines) {
            a=[];
            a.push(border);
            a=a.concat(line.split(''));
            a.push(border);
            result.push(a);
        }
        a=new Array(width+2);
        a.fill(border);
        result.push(a);
        return result;
    }
    else {
        return lines.map(l=>l.split(''));
    }
}

function getAsNumberMatrix(year, day, separator=',', border=null) {

}

exports.getFirstLine=getFirstLine;
exports.getLines=getLines;
exports.getAsCharMatrix=getAsCharMatrix;
exports.getAsNumberMatrix=getAsNumberMatrix;