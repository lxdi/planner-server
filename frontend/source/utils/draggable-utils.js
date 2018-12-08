import {swapLL, insertLL, removeFromLL, addToLastLL} from './linked-list'

export const mergeArrays = function(mainArr, arr2){
  for(var i in arr2){
    if(mainArr.indexOf(arr2[i])<0){
      mainArr.push(arr2[i])
    }
  }
}

// dragState{all, root}
export const replaceDraggableUtil = function(newParent, targetObj, objToDrag, dragState){
  const altered = []
  if(objToDrag!=null && objToDrag!=targetObj && !isNodeDescendsFrom(dragState.all, targetObj, objToDrag)){
    const oldParent = objToDrag.parentid!=null? dragState.all[objToDrag.parentid]:null
    if(oldParent==null && newParent==null){
      //swap within root
       mergeArrays(altered, swapLL(dragState.root, objToDrag, targetObj))
    }
    if(oldParent==null && newParent!=null){
      //insert to new parent
      mergeArrays(altered, removeFromLL(dragState.root, objToDrag))
      mergeArrays(altered, insertLL(newParent.children, targetObj, objToDrag))
      objToDrag.parentid = newParent.id
    }
    if(oldParent!=null && newParent==null){
      //insert to root
      mergeArrays(altered, removeFromLL(oldParent.children, objToDrag))
      mergeArrays(altered, insertLL(dragState.root, targetObj, objToDrag))
      objToDrag.parentid = null
    }
    if(oldParent!=null && newParent!=null){
      if(oldParent==newParent){
        //swap within new parent (or within old parent)
        mergeArrays(altered, swapLL(oldParent.children, objToDrag, targetObj))
      } else {
        //insert to the new parent
        mergeArrays(altered, removeFromLL(oldParent.children, objToDrag))
        mergeArrays(altered, insertLL(newParent.children, targetObj, objToDrag))
        objToDrag.parentid = newParent.id
      }
    }
  }
  return altered
}

// dragState{all, root}
export const addAsChildDraggableUtil = function(parent, objToDrag, dragState){
  const altered = []
  const oldParent = objToDrag.parentid!=null? dragState.all[objToDrag.parentid]:null
  if(objToDrag!=null && !isNodeDescendsFrom(dragState.all, parent, objToDrag) && parent!=oldParent){
    if(oldParent!=null){
      mergeArrays(altered, removeFromLL(oldParent.children, objToDrag))
    }else {
      mergeArrays(altered, removeFromLL(dragState.root, objToDrag))
    }
    if(parent!=null){
      mergeArrays(altered, addToLastLL(parent.children, objToDrag))
      objToDrag.parentid = parent.id
    } else {
      mergeArrays(altered, addToLastLL(dragState.root, objToDrag))
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
