let pwd='hepxcrrq'.split('');

const pwdLen=pwd.length;

function nextPwd(pwd) {
    let pos=pwdLen-1;
    while (pos>=0) {
        if (pwd[pos]==='z') {
            pwd[pos]='a';
            pos--;
        }
        else {
            pwd[pos]=String.fromCharCode(pwd[pos].charCodeAt(0)+1);
            break;
        }
    }
    return pwd;
}

function hasRun(pwd) {
    let lastChar=0;
    let num=1;
    for (let c of pwd) {
        let char=c.charCodeAt(0);
        if (char===lastChar+1) {
            num++;
            if (num===3) {
                return true;
            }
        }
        else {
            num=1;
        }
        lastChar=char;
    }
    return false;
}

function hasPairs(pwd) {
    let pos=-1;
    // we must skip the last two letter for the first pair
    for (let i=0;i<pwdLen-2;i++) {
        if (pwd[i]===pwd[i+1]) {
            pos=i;
            break;
        }
    }
    if (-1===pos) {
        return false;
    }
    for (let i=pos+2;i<pwdLen-1;i++) {
        if (pwd[i]===pwd[i+1]) {
            return true;
        }
    }
    return false;
}

function isValid(pwd) {
    if (pwd.includes('i') || pwd.includes('o') || pwd.includes('l'))
        return false;
    if (!hasRun(pwd)) {
        return false;
    }
    if (!hasPairs(pwd)) {
        return false;
    }
    return true;
}

while (true) {
    pwd=nextPwd(pwd);
    if (isValid(pwd))
        break;
}
console.log(pwd.join());