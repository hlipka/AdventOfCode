const fs = require('fs');

fs.readFile('../data/day01.txt', 'utf8', (err, data) => {
    if (err) {
        console.error(err);
        return;
    }
    let floor=0;
    let pos=1;
    for (const c of data) {
        if (c==='(') floor++;
        else if (c===')') floor--;
        if (floor===-1)
        {
            console.log(pos);
            break;
        }
        pos++;
    }
});

