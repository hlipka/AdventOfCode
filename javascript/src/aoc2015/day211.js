/*
Boss:
Hit Points: 109
Damage: 8
Armor: 2
 */
/*
shop
Weapons:    Cost  Damage  Armor
Dagger        8     4       0
Shortsword   10     5       0
Warhammer    25     6       0
Longsword    40     7       0
Greataxe     74     8       0

Armor:      Cost  Damage  Armor
Leather      13     0       1
Chainmail    31     0       2
Splintmail   53     0       3
Bandedmail   75     0       4
Platemail   102     0       5

Rings:      Cost  Damage  Armor
Damage +1    25     1       0
Damage +2    50     2       0
Damage +3   100     3       0
Defense +1   20     0       1
Defense +2   40     0       2
Defense +3   80     0       3
 */

let weapons=[[8,4,0],[10,5,0],[25,6,0],[40,7,0],[74,8,0],]
let armor=[[13,0,1],[31,0,2],[53,0,3],[75,0,4],[102,0,5],]
let rings=[[25,1,0],[50,2,0],[100,3,0],[20,0,1],[40,0,2],[80,0,1]]

let bestCost=1000;

function simulate(damage, armor) {
    let boss=109
    let us=100

    let usDpR=Math.max(1, damage-2); // boss has 2 armor
    let bossDpR=Math.max(1, 8-armor); // boss does 8 damage

    let bossDownRound=Math.ceil(boss/usDpR);
    let usDownRound=Math.ceil(us/bossDpR);

    return usDownRound>=bossDownRound;
}

function simulateRings(damage, armor, cost, r1, r2) {
    if (r1!==-1) {
        damage+=rings[r1][1];
        armor+=rings[r1][2];
        cost+=rings[r1][0];
    }
    if (r2!==-1) {
        damage+=rings[r2][1];
        armor+=rings[r2][2];
        cost+=rings[r2][0];
    }
    if (cost>=bestCost) return; // when we are too expensive, skip the fight
    if (simulate(damage, armor)) {
        bestCost=cost;
        console.log('won for',cost);
    }
}

function simulateAllRings(damage, armor, cost) {
    simulateRings(damage, armor, cost, -1, -1);
    simulateRings(damage, armor, cost, 0, -1);
    simulateRings(damage, armor, cost, 1, -1);
    simulateRings(damage, armor, cost, 2, -1);
    simulateRings(damage, armor, cost, 3, -1);
    simulateRings(damage, armor, cost, 4, -1);
    simulateRings(damage, armor, cost, 5, -1);
    simulateRings(damage, armor, cost, 0, 1);
    simulateRings(damage, armor, cost, 0, 2);
    simulateRings(damage, armor, cost, 0, 3);
    simulateRings(damage, armor, cost, 0, 4);
    simulateRings(damage, armor, cost, 0, 5);
    simulateRings(damage, armor, cost, 1, 2);
    simulateRings(damage, armor, cost, 1, 3);
    simulateRings(damage, armor, cost, 1, 4);
    simulateRings(damage, armor, cost, 1, 5);
    simulateRings(damage, armor, cost, 2, 3);
    simulateRings(damage, armor, cost, 2, 4);
    simulateRings(damage, armor, cost, 2, 5);
    simulateRings(damage, armor, cost, 3, 4);
    simulateRings(damage, armor, cost, 3, 5);
    simulateRings(damage, armor, cost, 4, 5);
}

console.log('fight!')
for (let w of weapons) {
    // also simulate without armor
    simulateAllRings(w[1], 0, w[0]);
    for (let a of armor) {
        simulateAllRings(w[1], a[2], w[0] + a[0]);
    }
}

console.log("best rig:",bestCost);