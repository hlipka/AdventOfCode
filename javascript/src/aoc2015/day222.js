const { dfs } = require('../util/search');

const bossHits=58;
const bossDamage=9;
//const bossHits=14;
//const bossDamage=8;

const missileCost=53;
const missileDamage=4;

const drainCost=73;
const drainDamage=2;
const drainHeal=2;

const shieldCost=113;
const shieldTime=6;
const shieldArmour=7;

const poisonCost=173;
const poisonTime=6;
const poisonDamage=3;

const rechargeCost=229;
const rechargeTime=5;
const rechargeMana=101;

// potential optimization: just store the remaining times for the 'Shield, 'Poison' and 'Recharge' effects, instead of an array
function state(mana, usedMana, hits, bossHits, shield, poison, recharge) {
    let s = Object.create(state.methods);
    s.mana=mana;
    s.usedMana=usedMana;
    s.hits=hits;
    s.bossHits=bossHits;
    s.shield=shield;
    s.poison=poison;
    s.recharge=recharge;
    return s;
}
state.methods = {
    getArmour() {
        return this.shield>0?shieldArmour:0;
    },
    applyEffects() {
        if (this.poison>0) {
            this.bossHits -= poisonDamage;
        }
        if (this.recharge>0) {
            this.mana+=rechargeMana;
        }
    },
    wearEffects() {
        if (this.poison>0) {
            this.poison--;
        }
        if (this.recharge>0) {
            this.recharge--;
        }
        if (this.shield>0) {
            this.shield--;
        }
    },
    addEffect(type) {
        switch (type) {
            case 'M':
                this.bossHits-=missileDamage;
                this.mana-=missileCost;
                this.usedMana+=missileCost;
                break;
            case 'D':
                this.bossHits-=drainDamage;
                this.hits+=drainHeal;
                this.mana-=drainCost;
                this.usedMana+=drainCost;
                break;
            case 'S':
                this.shield=shieldTime;
                this.mana-=shieldCost;
                this.usedMana+=shieldCost;
                break;
            case 'P':
                this.poison=poisonTime;
                this.mana-=poisonCost;
                this.usedMana+=poisonCost;
                break;
            case 'R':
                this.recharge=rechargeTime;
                this.mana-=rechargeCost;
                this.usedMana+=rechargeCost;
                break;
        }
        return this;
    },
}

let world= {
    leastMana: 100000,
    firstState() {
        // since we first do 'reachedTarget', which lets the boss attack, we add these hit-points to our hit-points first
        return state(500, 0, 50+bossDamage, bossHits, 0, 0, 0);
    },
    reachedTarget(state) {
        // basically this is the boss turn

        // when we are out of mana, we have lost (mana cannot go negative)
        // it also does not matter whether the boss is dead - we were not allowed to cast ths last spell
        if (state.mana<0) {
            return true;
        }
        // at this moment we decided which spell to cast (and did so)
        // effects of immediate spells were applied ('Magic Missile', 'Drain')
        // the boss might be dead by this, so we check this first
        if (state.bossHits<=0) {
            this.leastMana = Math.min(state.usedMana, this.leastMana);
            return true;
        }
        // the boss turn starts now, so first we apply the effects of any active spells
        state.applyEffects();

        // the boss might be dead now
        if (state.bossHits<=0) {
            this.leastMana = Math.min(state.usedMana, this.leastMana);
            return true;
        }
        // now the boss fights
        state.hits-=Math.max(1, bossDamage-state.getArmour());
        state.wearEffects();

        // check whether we are dead
        if (state.hits<0) {
            return true;
        }
        return state.hits <= 0;

    },
    canPrune(state) {
        return state.usedMana>this.leastMana;
    },
    copyState(aState) {
        return state(aState.mana, aState.usedMana, aState.hits, aState.bossHits, aState.shield, aState.poison, aState.recharge);
    },
    newStates(state) {
        // basically this is the player turn

        // 'hard' mode: loose one hit point
        state.hits--;
        if (state.hits<=0) {
            return [];
        }

        // first, apply effects of any active spells
        state.applyEffects();
        state.wearEffects();

        // when the boss is dead now, we don't need to do anything, because we won
        if (state.bossHits<=0) {
            this.leastMana = Math.min(state.usedMana, this.leastMana);
            return [];
        }
        // when we now cannot cast another spell, we have lost, so we don't do anything either (pruning this branch)
        if (state.mana<53) {
            return [];
        }
        let states=[]
        // we don't check for available mana here, this is done in 'reachedTarget'
        if (state.recharge===0) {
            states.push(this.copyState(state).addEffect('R'));
        }
        if (state.shield===0) {
            states.push(this.copyState(state).addEffect('S'));
        }
        if (state.poison===0) {
            states.push(this.copyState(state).addEffect('P'));
        }
        states.push(this.copyState(state).addEffect('M'));
        states.push(this.copyState(state).addEffect('D'));
        return states;
    }
}

dfs(world);

// 1538, 1362 is too high
console.log(world.leastMana);


