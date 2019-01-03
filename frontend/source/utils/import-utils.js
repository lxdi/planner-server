

// configsArr = [{arrName, posName}]
export const normalizeInnerArrays = function(obj, configsArr, idx){
    if(idx==null){
      idx = 0
    }
    if(obj!=null && configsArr[idx]!=null){
      const newArr = []
      for(var i in obj[configsArr[idx].arrName]){
        const childObj = obj[configsArr[idx].arrName][i]
        normalizeInnerArrays(childObj, configsArr, idx+1)
        newArr[childObj[configsArr[idx].posName]] = childObj
      }
      obj[configsArr[idx].arrName] = newArr
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
