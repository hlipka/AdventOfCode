const row=3010;
const col=3019;

const start=20151125;
const factor=252533;
const modulo=33554393;

function getNumFromPos(row, col) {
    let pos=1;
    for (let i=1;i<row;i++) {
        pos+=i;
    }
    for (let i=1;i<col;i++) {
        pos+=(i+row);
    }
    return pos;
}

// which number do we actually search?
const numPos=getNumFromPos(row, col);

function getNum(numPos) {
    let num=start;
    if (numPos===1) {
        return start;
    }
    for (let i=1;i<numPos;i++) {
        let a=num*factor;
        num=a % modulo;
    }
    return num;
}
console.log(getNum(1));
console.log(getNum(2));
console.log(getNum(3));
console.log(getNum(4));
console.log(getNum(5));
console.log(getNum(getNumFromPos(6,2)));
console.log();
console.log(numPos);
console.log(getNum(numPos));
