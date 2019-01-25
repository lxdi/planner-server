

// configsArr = [{arrName, posName}]
export const normalizeInnerArrays = function(obj, configsArr, idx){
    if(idx==null){
      idx = 0
    }
    if(obj!=null && configsArr[idx]!=null){
      const newArr = makeMap(obj[configsArr[idx].arrName], configsArr[idx].posName, (childObj)=>normalizeInnerArrays(childObj, configsArr, idx+1))
      obj[configsArr[idx].arrName] = newArr
    }
}

export const makeMap = function(arr, fieldName, callback){
  const result = []
  for(var i in arr){
    result[arr[i][fieldName]] = arr[i]
    if(callback!=null){
      callback(arr[i])
    }
  }
  return result
}

export const makeSplitMap = function(arr, fieldName, splitterFieldName){
  const result = []
  for(var i in arr){
    if(result[arr[i][splitterFieldName]]==null){
      result[arr[i][splitterFieldName]] = []
    }
    result[arr[i][splitterFieldName]][arr[i][fieldName]] = arr[i]
  }
  return result
}

export const getFromMappedRepByid = function(rep, sid){
  for(var superid in rep){
    for(var id in rep[superid]){
      if(rep[superid][id].id === sid){
        return rep[superid][id]
      }
    }
  }
  return null
}

export const iterateMappedRep = function(rep, callback){
  for(var sid in rep){
    for(var id in rep[sid]){
      callback(rep[sid][id])
    }
  }
}

export const getMaxVal = function(objects, fieldName){
    var result = 0
    if(objects!=null){
      for(var objid in objects){
        if(objects[objid][fieldName]>result){
          result = objects[objid][fieldName]
        }
      }
    }
    return result
}

export const sortByField = function(objs, fieldName){
  const result = []
  for(var i in objs){
    result[objs[i][fieldName]] = objs[i]
  }
  return result
}

export const deleteNode = function(nodes, node){
  for(var id in nodes){
    if(nodes[id].nextid == node.id){
      nodes[id].nextid = node.nextid
      break
    }
  }
  delete nodes[node.id]
}
