import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from './data-constants'
import {CreateMean, CreateLayer} from './creators'

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
