const num=29000000/10;

let house=1;

function calculatePresent(house) {
    let presents=0;
    // is there a faster way to get all dividers?
    // we can test all primes until sqrt(house) to get the prime factors, but we need all real dividers
    // there is a recursive function to calculate this sum, but its implementation might take longer
    for (let i=1;i<house/2+1;i++) {
        if (0===(house%i)) {
            presents+=i;
        }
    }
    presents+=house
    return presents;
}

while (calculatePresent(house)<num) {
    house++
    if (0===(house%1000)) console.log(house);
    if (house>num) {
        console.log("abort");
        break;
    }
}

// 776160 is too high

console.log("house=", house);

// 2900000
//