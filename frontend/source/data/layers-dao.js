import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('layers-dao', 'layers-request', (stateSetter, mean)=>{
  if(mean.id!=null && mean.id>0){
    $.ajax({url: "layer/get/bymean/"+mean.id}).then(function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importLayers(stateSetter, mean, data)
              errorCatcherForAsync(()=>fireEvent('layers-dao', 'layers-received', [mean, data]))
            });
  } else {
    fireEvent('layers-dao', 'layers-received', [mean])
  }
})

registerEvent('layers-dao', 'layers-received', (stateSetter, mean, layers)=>[mean, layers])

// registerEvent('layers-dao', 'create', (stateSetter, mean)=>{
//   $.ajax({url: "/layer/create/"+mean.id}).then(function(data) {
//             var receivedData = typeof data == 'string'? JSON.parse(data): data
//             viewStateVal('layers-dao', 'layers')[mean.id][data.id] = data
//             errorCatcherForAsync(()=>fireEvent('layers-dao', 'layer-created', [data]))
//           });
// })
//
// registerEvent('layers-dao', 'layer-created', (stateSetter, layer)=>[layer])

registerEvent('layers-dao', 'create-candidate', (stateSetter, mean, layer)=>{
  if(viewStateVal('layers-dao', 'layers-candidates')==null){
    stateSetter('layers-candidates', [])
  }
  layer.priority = getNextLayerPriority(mean)
  viewStateVal('layers-dao', 'layers-candidates').push(layer)
})

registerEvent('layers-dao', 'save-candidates', (stateSetter, mean)=>{
  const layers = viewStateVal('layers-dao', 'layers-candidates')
  if(layers!=null){
    for(var layerid in layers){
      layers[layerid].meanid = mean.id
    }
    $.ajax({
      url: '/layer/create/list',
      type: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify(layers),
      success: function(data) {
        var receivedData = typeof data == 'string'? JSON.parse(data): data
        importLayers(stateSetter, mean, data)
        stateSetter('layers-candidates', null)
        errorCatcherForAsync(()=>fireEvent('layers-dao', 'layers-many-created', [mean, data]))
      }
    });
  } else {
    fireEvent('layers-dao', 'layers-many-created', [mean])
  }
})

registerEvent('layers-dao', 'layers-many-created', (stateSetter, mean, layer)=>[mean, layer])

registerReaction('layers-dao', 'mean-modal', 'close', (stateSetter)=>stateSetter('layers-candidates', null))
registerReaction('layers-dao', 'mean-modal', 'open', (stateSetter, mean)=>fireEvent('layers-dao', 'layers-request', [mean]))

const importLayers = function(stateSetter, mean, layers){
  if(viewStateVal('layers-dao', 'layers')==null){
      stateSetter('layers', {})
  }
  viewStateVal('layers-dao', 'layers')[mean.id] = {}
  const layersForMean = viewStateVal('layers-dao', 'layers')[mean.id]
  for(var layeridx in layers){
    layersForMean[layers[layeridx].id] = layers[layeridx]
  }
}

const getNextLayerPriority = function(mean){
  var result = 0
  const maxInCandidates = getMaxLayerPriorityOfLayers(viewStateVal('layers-dao', 'layers-candidates'))
  if(maxInCandidates!=0){
    return maxInCandidates+1
  } else {
    if(viewStateVal('layers-dao', 'layers')!=null){
      return getMaxLayerPriorityOfLayers(viewStateVal('layers-dao', 'layers')[mean.id])+1
    } else {
      return 1
    }
  }
}

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

const errorCatcherForAsync = function(callback){
  try{
    callback()
  } catch (e){
    console.error(e)
  }
}
