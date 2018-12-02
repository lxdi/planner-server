//import $ from 'jquery'
import {sendGet} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('layers-dao', 'layers-request', (stateSetter, mean)=>{
  if(mean.id!=null && mean.id>0){
    sendGet("layer/get/bymean/"+mean.id, function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              //importLayers(stateSetter, mean, data)
              mean.layers = []
              if(data.length>0){
                for(var layerindx in data){
                  layerAlign(data[layerindx])
                  mean.layers[data[layerindx].priority] = data[layerindx]
                }
              }
              errorCatcherForAsync(()=>fireEvent('layers-dao', 'layers-received', [mean, data]))
            })
  } else {
    mean.layers = []
    fireEvent('layers-dao', 'layers-received', [mean])
  }
})

registerEvent('layers-dao', 'layers-received', (stateSetter, mean, layers)=>[mean, layers])

registerEvent('layers-dao', 'add-layer', (stateSetter, mean)=>{
  const layer = {
    priority: getMaxLayerPriorityOfLayers(mean.layers)+1
  }
  mean.layers[layer.priority] = layer
})

registerReaction('layers-dao', 'mean-modal', 'open', (stateSetter, mean)=>fireEvent('layers-dao', 'layers-request', [mean]))


const getMaxLayerPriorityOfLayers = function(layers){
    var result = 0
    if(layers!=null){
      for(var layerid in layers){
        if(layers[layerid].priority>result){
          result = layers[layerid].priority
        }
      }
    }
    return result
}

const layerAlign = function(layer){
  const subjectsAligned = []
  for(var subjId in layer.subjects){
    subjectAlign(layer.subjects[subjId])
    subjectsAligned[layer.subjects[subjId].position] = layer.subjects[subjId]
  }
  layer.subjects = subjectsAligned
}

const subjectAlign = function(subject){
  const tasksAligned = []
  for(var taskId in subject.tasks){
    tasksAligned[subject.tasks[taskId].position] = subject.tasks[taskId]
  }
  subject.tasks = tasksAligned
}

const errorCatcherForAsync = function(callback){
  try{
    callback()
  } catch (e){
    console.error(e)
  }
}
