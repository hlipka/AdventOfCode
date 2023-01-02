import {getLines} from '../util/parse.js';

function getLen(line) {
    let line2='"'+line.replaceAll('\\','\\\\').replaceAll('"','\\"')+'"';
    let len = line2.length;
    console.log('transformed ['+line+'] int ['+line2+'], len=',len)
    return len;
}
let data=getLines("2015", "08");
let dataLen=data.map(l=>l.length).reduce((a,b)=>a+b);
let memLen=data.map(l=>getLen(l)).reduce((a,b)=>a+b);
console.log(dataLen);
console.log(memLen);
console.log(memLen-dataLen);

// 1446 is too low

