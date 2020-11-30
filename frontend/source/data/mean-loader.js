import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from './data-constants'
import {CreateMean, CreateLayer} from './creators'

export const prepareMean = function(mean){
  if(mean.layers!=null){
    return true
  }

  if(mean.id == DataConstants.newId){
    mean.layers = []
    addNewLayerToMean(mean)
    return true
  }

  if(!prepareLayers(mean)){
    return false
  }

  return prepareTasks(mean)
}

const prepareLayers = function(mean){
  const layersByMean = chkSt(DataConstants.layerRep, DataConstants.indexByMean)

  if(layersByMean==null){
    fireEvent(DataConstants.layerRep, DataConstants.byMeanRequest, [mean.id])
    return false
  }

  const layers = chkSt(DataConstants.layerRep, DataConstants.indexByMean)[mean.id]

  if(layers==null){
    mean.layers = []
    return true
  }

  mean.layers = getEntriesAsList(layers)
  return true
}

const prepareTasks = function(mean){
  const allTasksByMean = chkSt(DataConstants.taskRep, DataConstants.indexByMean)

  if(allTasksByMean == null){
    fireEvent(DataConstants.taskRep, DataConstants.byMeanRequest, [mean.id])
    return false
  }

  const layers = mean.layers
  layers.forEach(layer => layer.tasks = [])
  const tasks = chkSt(DataConstants.taskRep, DataConstants.indexByMean)[mean.id]

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
  mean.layers.push(CreateLayer(DataConstants.newId, priority, mean.id))
}
