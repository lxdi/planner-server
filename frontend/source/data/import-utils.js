

// configsArr = [{arrName, posName}]
export const normalizeInnerArrays = function(obj, configsArr, idx){
    if(configsArr[idx]!=null){
      const newArr = []
      for(var i in configsArr.obj[configsArr.arrName]){
        const childObj = configsArr.obj[configsArr.arrName][i]
        normalizeArray(childObj, configsArr, idx+1)
        newArr[childObj[configsArr.posName]] = childObj
      }
      configsArr.obj[configsArr.arrName] = newArr
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
