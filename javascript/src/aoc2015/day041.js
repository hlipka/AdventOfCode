import { createHash } from 'node:crypto'

const key='ckczppom';
let num=1;
while (true) {
    let hash = createHash('md5').update(key+num).digest("hex");
    if (hash.startsWith('00000')) {
        console.log(num);
        break;
    }
    num++;
}
