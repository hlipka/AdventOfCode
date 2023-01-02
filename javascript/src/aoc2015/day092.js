import {getLines} from '../util/parse.js';

function getRoute(line) {
    let parts=line.match(/(\S+) to (\S+) = (\d+)/);
    return {from:parts[1], to:parts[2], len:Number(parts[3])};
}

let routes=getLines("2015","09").map(l=>getRoute(l));

// nodes is an object, with each node as a property keys, and the actual nodes as value
// the nodes themselves have all reachable targets as property keys, and the distance as value
let nodes={}
let nodeNames=[];
for (let route of routes){
    let from=nodes[route.from];
    if (!from) {
        from={};
        nodes[route.from]=from;
        nodeNames.push(route.from);
    }
    from[route.to]=route.len;
    let to=nodes[route.to];
    if (!to) {
        to={};
        nodes[route.to]=to;
        nodeNames.push(route.to);
    }
    to[route.from]=route.len;
}
console.table(nodes);
console.log(nodeNames);
let maxLen=0;
let nodeCount=nodeNames.length;
// use each node as start node
for (let n of nodeNames) {
    // start a visit through them, with the current node already visited
    let node = nodes[n];
    lookAt(node, new Set([n]), 0);
}

console.log(maxLen);

function lookAt(node, visited, length) {
    // when we have visited all nodes, we are done
    if (visited.size===nodeCount) {
        if (length>maxLen) {
            console.log("new best is "+length);
            maxLen=length;
        }
    }
    // now visit all nodes reachable from the current node
    for (let t in node) {
        // when we were already there, skip it
        if (visited.has(t))
            continue;
        let visitNext=new Set(visited);
        visitNext.add(t);
        lookAt(nodes[t], visitNext, length+node[t]);
    }
}
