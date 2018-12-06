
/*
  1) ListNode must contain id and nextid
  2) nodes in Nodes array must be accessed by node.id
*/

export const iterateLL = function(nodes, currentNode, callback){
  callback(currentNode)
  iterateLL(nodes, nodes[current.nextid], callback)
}

//elements must in elements must be accessed by element.id
export const findHead = function(nodes){
  for(var i in nodes){
    var isHead = true
    for(var j in nodes){
      if(nodes[i]!=nodes[j] && nodes[nodes[j].nextid]==nodes[i]){
        isHead = false
      }
    }
    if(isHead){
      return nodes[i]
    }
  }
}

export const findLast = function(nodes){
  for(var i in nodes){
    if(nodes[i].nextid==null){
      return nodes[i]
    }
  }
}
