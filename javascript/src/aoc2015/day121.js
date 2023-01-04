import {getFirstLine, getLines} from '../util/parse.js';

const json=JSON.parse(getFirstLine("2015","12"));

function traverse(json) {
    let result=0;
    Object.entries(json).forEach(([key, value]) => {
        if (typeof(value) === 'object') {
            result+=traverse(value);
        }
        else if (Array.isArray(value)) {
            result+=value.reduce((a,b)=>a+b, 0);
        }
        else if (typeof(value) === 'number') {
            result+=value;
        }
        console.log('found',key)
        // do something with key and val
    });
    return result;
}

const sum = traverse(json);

console.log(sum);
