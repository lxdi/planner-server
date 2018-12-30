
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
  const isContinue = callback(currentNode)
  if((isContinue==null || (isContinue!=null && isContinue==true)) && currentNode.nextid!=null){
    const next = nodes[currentNode.nextid]
    iterateLL(nodes, next, callback)
  }
}

export const iterateLLReverse = function(nodes, currentNode, callback){
  const isContinue = callback(currentNode)
  if((isContinue==null || (isContinue!=null && isContinue==true)) && currentNode.previd!=null){
    const next = nodes[currentNode.previd]
    iterateLLReverse(nodes, next, callback)
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

export const findPrev = function(nodes, obj){
  for(var id in nodes){
    if(nodes[id].nextid==obj.id){
      return nodes[id]
    }
  }
  return null
}

export const swapLL = function(nodes, obj1, obj2){
  const altered = []
  if(obj1!=obj2){
    const prevObj1 = findPrev(nodes, obj1)
    const prevObj2 = findPrev(nodes, obj2)
    if(prevObj1!=null && prevObj1!=obj1 && prevObj1!=obj2){
      prevObj1.nextid=obj2.id
      altered.push(prevObj1)
    }
    if(prevObj2!=null && prevObj2!=obj1 && prevObj2!=obj2){
      prevObj2.nextid=obj1.id
      altered.push(prevObj2)
    }

    if(obj1.nextid==obj2.id){
      obj1.nextid = obj2.nextid
      obj2.nextid = obj1.id
    } else {
      if(obj2.nextid == obj1.id){
        obj2.nextid = obj1.nextid
        obj1.nextid = obj2.id
      } else {
        const tempNextid = obj1.nextid
        obj1.nextid = obj2.nextid
        obj2.nextid = tempNextid
      }
    }
    altered.push(obj1)
    altered.push(obj2)
  }
  return altered
}

export const insertLL = function(nodes, targetObj, sourceObj){
  const altered = []
  if(targetObj!=sourceObj){
    const prevTargetObj = findPrev(nodes, targetObj)
    if(prevTargetObj!=null && prevTargetObj!=sourceObj){
      prevTargetObj.nextid = sourceObj.id
      altered.push(prevTargetObj)
    }
    nodes[sourceObj.id] = sourceObj
    sourceObj.nextid = targetObj.id
    altered.push(sourceObj)
    altered.push(targetObj)
  }
  return altered
}

export const removeFromLL = function(nodes, obj){
  const altered = []
  const prevObj = findPrev(nodes, obj)
  if(prevObj!=null){
      prevObj.nextid = obj.nextid
      altered.push(prevObj)
  }
  delete nodes[obj.id]
  obj.nextid = null
  obj.parentid = null
  altered.push(obj)
  return altered
}

export const addToLastLL = function(nodes, obj){
  const altered = []
  const last = findLast(nodes)
  if(last != null){
    last.nextid = obj.id
    altered.push(last)
  }
  if(nodes!=null){
    nodes[obj.id] = obj
  }
  obj.nextid = null
  altered.push(obj)
  return altered
}
