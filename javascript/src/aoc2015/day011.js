const fs = require('fs');

fs.readFile('../../../data/2015/day01.txt', 'utf8', (err, data) => {
    if (err) {
        console.error(err);
        return;
    }
    let floor=0;
    for (const c of data) {
        if (c==='(') floor++;
        else if (c===')') floor--;
    }
    console.log(floor);
});

