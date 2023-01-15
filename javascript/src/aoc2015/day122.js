const {getFirstLine} =require("../util/parse");

const json=JSON.parse(getFirstLine("2015","12"));

function traverse(json) {
    if ((typeof(json)==='object') && !Array.isArray(json) && Object.entries(json).some(([, value]) => {
        return (value==='red');

        }))
    {
        return 0;
    }
    let result=0;
    Object.entries(json).forEach(([, value]) => {
        if (typeof(value) === 'object') {
            result+=traverse(value);
        }
        else if (Array.isArray(value)) {
            result+=value.reduce((a,b)=>a+b, 0);
        }
        else if (typeof(value) === 'number') {
            result+=value;
        }
        // do something with key and val
    });
    return result;
}

const sum = traverse(json);

// 34744 is too low

console.log(sum);
