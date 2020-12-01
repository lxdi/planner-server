import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from './data-constants'
import {CreateMean, CreateLayer} from './creators'

export const prepareMean = function(mean){
  if(mean.isFull){
    return true
  }

  if(mean.id == DataConstants.newId){
    mean.layers = []
    addNewLayerToMean(mean)
    mean.isFull = true
    return true
  }

  if(!checkReps(mean)){
    return false
  }

  if(!prepareLayers(mean)){
    return false
  }

  if(!prepareTasks(mean)){
    return false
  }

  if(!prepareWhateverInTask(mean, DataConstants.topicRep, 'topics')){
    return false
  }

  if(!prepareWhateverInTask(mean, DataConstants.tasktestingRep, 'taskTestings')){
    return false
  }

  mean.isFull = true

  return true
}

const checkReps = function(mean){
  var result = true
  if(chkSt(DataConstants.layerRep, DataConstants.indexByMean)==null){
    fireEvent(DataConstants.layerRep, DataConstants.byMeanRequest, [mean.id])
    result = false
  }

  if(chkSt(DataConstants.taskRep, DataConstants.indexByMean)==null){
    fireEvent(DataConstants.taskRep, DataConstants.byMeanRequest, [mean.id])
    result = false
  }

  if(chkSt(DataConstants.topicRep, DataConstants.indexByMean)==null){
    fireEvent(DataConstants.topicRep, DataConstants.byMeanRequest, [mean.id])
    result = false
  }

  if(chkSt(DataConstants.tasktestingRep, DataConstants.indexByMean)==null){
    fireEvent(DataConstants.tasktestingRep, DataConstants.byMeanRequest, [mean.id])
    result = false
  }

  return result
}

const prepareLayers = function(mean){

  const layers = chkSt(DataConstants.layerRep, DataConstants.indexByMean)[mean.id]

  if(layers==null){
    mean.layers = []
    return true
  }

  mean.layers = getEntriesAsList(layers)
  return true
}

const prepareTasks = function(mean){

  const layers = mean.layers
  layers.forEach(layer => layer.tasks = [])
  const tasks = chkSt(DataConstants.taskRep, DataConstants.indexByMean)[mean.id]

  if(tasks == null){
    return true
  }

  const layersMap = chkSt(DataConstants.layerRep, DataConstants.indexByMean)[mean.id]

  for(const id in layersMap){
    if(layersMap[id].tasks==null){
      layersMap[id].tasks = []
    }
  }

  for(const id in tasks){
    const layerid = tasks[id].layerid
    layersMap[layerid].tasks.push(tasks[id])
  }

  return true
}

const prepareWhateverInTask = function(mean, repName, fieldName){

  const objMap = chkSt(repName, DataConstants.indexByMean)[mean.id]

  if(objMap == null){
    return true
  }

  const tasksMap = chkSt(DataConstants.taskRep, DataConstants.indexByMean)[mean.id]

  for(const id in tasksMap){
    if(tasksMap[id][fieldName]==null){
      tasksMap[id][fieldName] = []
    }
  }

  for(const id in objMap){
    const taskid = objMap[id].taskid
    const task = tasksMap[taskid]
    if(!task[fieldName].includes(objMap[id])){
      task[fieldName].push(objMap[id])
    }
  }

  return true
}

const getEntriesAsList = function(obj){
  const result = []
  for(const id in obj){
    result.push(obj[id])
  }
  return result
}

export const addNewLayerToMean = function(mean){
  var priority = 0
  for(const id in mean.layers){
    if(mean.layers[id].priority>priority){
      priority = mean.layers[id].priority
    }
  }
  priority = priority + 1
  mean.layers.push(CreateLayer(DataConstants.newId, priority, mean.id))
}
