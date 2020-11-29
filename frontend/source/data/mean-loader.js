import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {dataConstants} from './data-constants'
import {CreateMean, CreateLayer} from './creators'

export const prepareMean = function(mean){
  if(mean.layers!=null){
    return true
  }

  if(mean.id == dataConstants.newId){
    mean.layers = []
    return true
  }

  const layersByMean = chkSt(dataConstants.layerRep, dataConstants.indexByMean)
  if(layersByMean==null){
    fireEvent(dataConstants.layerRep, dataConstants.byMeanRequest, [mean.id])
    return false
  }

  const layers = chkSt(dataConstants.layerRep, dataConstants.indexByMean)[mean.id]

  if(layers==null){
    mean.layers = []
    return true
  }
  mean.layers = getEntriesAsList(layers)
  mean.layers.forEach(layer => layer.tasks = [])

  const allTasksByMean = chkSt(dataConstants.taskRep, dataConstants.indexByMean)
  if(allTasksByMean == null){
    fireEvent(dataConstants.taskRep, dataConstants.byMeanRequest, [mean.id])
    return false
  }

  const tasks = chkSt(dataConstants.taskRep, dataConstants.indexByMean)[mean.id]

  if(tasks == null){
    return true
  }

  for(const id in tasks){
    const layerid = tasks[id].layerid
    layers[layerid].tasks.push(tasks[id])
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
  mean.layers.push(CreateLayer(dataConstants.newId, priority, mean.id))
}
