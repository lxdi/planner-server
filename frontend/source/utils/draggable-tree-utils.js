import {swapLL, insertLL, removeFromLL, addToLastLL} from './linked-list'

export const mergeArrays = function(mainArr, arr2){
  for(var i in arr2){
    if(mainArr.indexOf(arr2[i])<0){
      mainArr.push(arr2[i])
    }
  }
}

// allNodes - map (id->node)
// dragState{all, root}
// cache{root, children}
export const replaceDraggableUtil = function(allnodes, newParentDeprecated, targetObj, objToDrag, cache){
  const altered = []
  const newParent = targetObj.parentid!=null? allnodes[targetObj.parentid]:null
  if(objToDrag!=null && objToDrag!=targetObj && !isNodeDescendsFrom(allnodes, targetObj, objToDrag)){
    if(cache==null){
      cache = resolveNodes(allnodes)
    }
    const oldParent = objToDrag.parentid!=null? allnodes[objToDrag.parentid]:null
    if(oldParent==null && newParent==null){
      //swap within root
       mergeArrays(altered, swapLL(cache.root, objToDrag, targetObj))
    }
    if(oldParent==null && newParent!=null){
      //insert to new parent
      mergeArrays(altered, removeFromLL(cache.root, objToDrag))
      mergeArrays(altered, insertLL(cache.children[newParent.id], targetObj, objToDrag))
      objToDrag.parentid = newParent.id
    }
    if(oldParent!=null && newParent==null){
      //insert to root
      mergeArrays(altered, removeFromLL(cache.children[oldParent.id], objToDrag))
      mergeArrays(altered, insertLL(cache.root, targetObj, objToDrag))
      objToDrag.parentid = null
    }
    if(oldParent!=null && newParent!=null){
      if(oldParent==newParent){
        //swap within new parent (or within old parent)
        mergeArrays(altered, swapLL(cache.children[oldParent.id], objToDrag, targetObj))
      } else {
        //insert to the new parent
        mergeArrays(altered, removeFromLL(cache.children[oldParent.id], objToDrag))
        mergeArrays(altered, insertLL(cache.children[newParent.id], targetObj, objToDrag))
        objToDrag.parentid = newParent.id
      }
    }
  }
  return altered
}

// dragState{all, root}
export const addAsChildDraggableUtil = function(allnodes, parent, objToDrag, cache){
  const altered = []
  const oldParent = objToDrag.parentid!=null? allnodes[objToDrag.parentid]:null
  if(objToDrag!=null && !isNodeDescendsFrom(allnodes, parent, objToDrag) && parent!=oldParent){
    if(oldParent!=null){
      mergeArrays(altered, removeFromLL(cache.children[oldParent.id], objToDrag))
    }else {
      mergeArrays(altered, removeFromLL(cache.root, objToDrag))
    }
    if(parent!=null){
      mergeArrays(altered, addToLastLL(cache.children[parent.id], objToDrag))
      objToDrag.parentid = parent.id
    } else {
      mergeArrays(altered, addToLastLL(cache.root, objToDrag))
      objToDrag.parentid = null
    }
  }
  return altered
}

const isNodeDescendsFrom =function(allnodes, child, searchParent){
  if(child==null || searchParent==null){
    return false
  }
  if(child==searchParent){
    return true
  }
  const parent = child.parentid!=null? allnodes[child.parentid]: null
  if(parent!=null){
    if(parent==searchParent){
      return true
    } else {
      return isNodeDescendsFrom(allnodes, parent, searchParent)
    }
  } else {
    return false
  }
}

//{root:[], children:[nodeid:[]]}
export const resolveNodes = function(nodes){
  const result = {root:{}, children:{}}
  for(var i in nodes){
    if(nodes[i].parentid!=null){
      if(result.children[nodes[i].parentid]==null){
        result.children[nodes[i].parentid] = {}
      }
      result.children[nodes[i].parentid][nodes[i].id] = nodes[i]
    } else {
      result.root[nodes[i].id] = nodes[i]
    }
  }
  return result
}
