
export const insertObj = function(targetObjParent, sourceObj, targetPosition, collectionName, positionName){
    const newArrayOfTasks = []
    for(var taskPos in targetObjParent[collectionName]){
      const task = targetObjParent[collectionName][taskPos]
      const currentTaskPos = task[positionName]
      if(currentTaskPos>=targetPosition){
        task[positionName] = task[positionName]+1
        if(currentTaskPos==targetPosition){
          sourceObj[positionName] = targetPosition
          newArrayOfTasks[sourceObj[positionName]] = sourceObj
        }
      }
      newArrayOfTasks[task[positionName]] = task
    }
    targetObjParent[collectionName] = newArrayOfTasks
}

export const deleteObj = function(objParent, objToDelete, collectionName, positionName){
  const newArrayOfTasks = []
  for(var taskPos in objParent[collectionName]){
    const task = objParent[collectionName][taskPos]
    const currentTaskPos = task[positionName]
    if(currentTaskPos>objToDelete[positionName]){
      task[positionName] = currentTaskPos-1
    }
    if(task!=objToDelete){
      newArrayOfTasks[task[positionName]] = task
    }
  }
  objParent[collectionName] = newArrayOfTasks
}

export const swapObjs = function(objParent, pos1, pos2, collectionName, positionName){
  const tempTask = objParent[collectionName][pos1]
  objParent[collectionName][pos1] = objParent[collectionName][pos2]
  objParent[collectionName][pos1][positionName] = pos1
  objParent[collectionName][pos2] = tempTask
  objParent[collectionName][pos2][positionName] = pos2
}
