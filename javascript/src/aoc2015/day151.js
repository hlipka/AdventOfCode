/*
Sugar: capacity 3, durability 0, flavor 0, texture -3, calories 2
Sprinkles: capacity -3, durability 3, flavor 0, texture 0, calories 9
Candy: capacity -1, durability 0, flavor 4, texture 0, calories 1
Chocolate: capacity 0, durability 0, flavor -2, texture 2, calories 8

 */

function getScore(sugar, sprinkles, candy, chocolate) {
    let cap = 3 * sugar - 3 * sprinkles - candy;
    let dur = 3 * sprinkles;
    let flav = 4 * candy - 2 * chocolate;
    let text = -3 * sugar + 2 * chocolate;

    return Math.max(cap, 0)*Math.max(dur, 0)*Math.max(flav, 0)*Math.max(text, 0);
}

let best = 0;
// lets brute-force all combinations
for (let sugar=1;sugar<98;sugar++) {
    for (let sprinkles=1;sprinkles<(99-sugar);sprinkles++) {
        for (let candy=1;candy<(100-sugar-sprinkles);candy++) {
            let score=getScore(sugar, sprinkles, candy, 100-sugar-sprinkles-candy);
            if (score>best) {
                best=score;
                console.log(best);
            }
        }
    }
}
// 10752 is too low
console.log("best=",best);
