const {getLines} =require("../util/parse");

let computer={
    code: [],
    pc: 0,
    a:1,
    b:0,
    hlf(cmd){
        if (cmd.arg1==='a') {
            this.a=this.a>>1;
        }
        else {
            this.b=this.b>>1;
        }
        this.pc++;
    },
    tpl(cmd) {
        if (cmd.arg1==='a') {
            this.a*=3;
        }
        else {
            this.b*=3;
        }
        this.pc++;
    },
    inc(cmd) {
        if (cmd.arg1==='a') {
            this.a++;
        }
        else {
            this.b++;
        }
        this.pc++;
    },
    jmp(cmd) {
        this.pc+=cmd.arg1;
    },
    jie(cmd) {
        if (cmd.arg1==='a') {
            if ((this.a%2)===0) {
                this.pc+=cmd.arg2;
            }
            else {
                this.pc++;
            }
        }
        else {
            if ((this.b%2)===0) {
                this.pc+=cmd.arg2;
            }
            else {
                this.pc++;
            }
        }
    },
    jio(cmd) {
        if (cmd.arg1==='a') {
            if (this.a===1) {
                this.pc+=cmd.arg2;
            }
            else {
                this.pc++;
            }
        }
        else {
            if (this.b===1) {
                this.pc+=cmd.arg2;
            }
            else {
                this.pc++;
            }
        }
    },
    state() {
        return "pc="+this.pc+", a="+this.a+", b="+this.b;
    },
    execute() {
        let maxPC=this.code.length;
        while (this.pc<maxPC) {
            let command=this.code[this.pc];
            console.log("executing",command);
            console.log("before:",this.state());
            this[command.cmd](command);
            console.log("after :",this.state());
        }
    }
}

function parse(line) {
    const command={};
    command.cmd=line.substring(0,3);
    const params=line.substring(3).split(',');
    if (isNaN(parseInt(params[0]))) {
        command.arg1=params[0].trim();
    }
    else {
        command.arg1=parseInt(params[0])
    }
    if (params.length===2) {
        if (isNaN(parseInt(params[1]))) {
            command.arg1=params[1].trim();
        }
        else {
            command.arg2=parseInt(params[1])
        }
    }
    return command;
}

computer.code=getLines('2015','23').map(l=>parse(l));

console.log(computer.code);

computer.execute();

// 9663 is too high
console.log(computer.a);
console.log(computer.b);

