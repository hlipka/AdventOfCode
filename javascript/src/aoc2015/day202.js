const num=29000000;

// we know this from a wrong answer :(
let house=700000;

function calculatePresent(house) {
    let presents=0;
    // since each elf will stop after 50 houses, we can start at 'house/50' since any earlier elf will have stopped already
    // e.g. at house 1000, elf 19 will have stopped at house 950, so 20 is the first one delivering to us
    // we can stop at the house halfway down, since anything higher will skip the current house
    for (let i=house/50;i<=house/2;i++) {
        if (0===(house%i)) {
            presents+=i*11;
        }
    }
    presents+=house*11
    return presents;
}

while (calculatePresent(house)<num) {
    house++
    if (0===(house%1000)) console.log(house);
    if (house>num/11) {
        console.log("abort");
        break;
    }
}

// too low: 702240 ?
console.log("house=", house);
