
function mutate(str) {
    let result='';
    let first=true;
    let current=' ';
    let count=0;
    for (let c of str) {
        if (first) {
            first=false;
            current=c;
            count=1;
        }
        else {
            if (c===current) {
                count++;
            }
            else {
                result +=count;
                result +=current;
                current=c;
                count=1;
            }
        }
    }
    result +=count;
    result +=current;
    return result;
}

let str='1321131112';

for (let i=0;i<50;i++) {
    str=mutate(str);
}

console.log(str.length);