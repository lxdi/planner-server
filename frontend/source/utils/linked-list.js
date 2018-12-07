
/*
  1) ListNode must contain id and nextid
  2) each Node in nodes array must be accessed by node.id
*/

export const iterateLLfull = function(nodes, callback){
  if(nodes!=null){
    const head = findHead(nodes)
    if(head!=null){
      iterateLL(nodes, head, callback)
    }
  }
}

export const iterateLL = function(nodes, currentNode, callback){
  callback(currentNode)
  if(currentNode.nextid!=null){
    const next = nodes[currentNode.nextid]
    iterateLL(nodes, next, callback)
  }
}

//elements must in elements must be accessed by element.id
export const findHead = function(nodes){
  for(var i in nodes){
    if(nodes[i]!=null){
      var isHead = true
      for(var j in nodes){
        if(nodes[j]!=null && nodes[i]!=nodes[j] && nodes[nodes[j].nextid]==nodes[i]){
          isHead = false
        }
      }
      if(isHead){
        return nodes[i]
      }
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
