import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('layers-dao', 'layers-request', (stateSetter, mean)=>{
  $.ajax({url: "layer/get/bymean/"+mean.id}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importLayers(stateSetter, mean, data)
            errorCatcherForAsync(()=>fireEvent('layers-dao', 'layers-received', [mean, data]))
          });
})

registerEvent('layers-dao', 'layers-received', (stateSetter, mean, layers)=>[mean, layers])

registerReaction('layers-dao', 'mean-modal', 'open', (stateSetter, mean)=>fireEvent('layers-dao', 'layers-request', [mean]))

registerEvent('layers-dao', 'create', (stateSetter, mean)=>{
  $.ajax({url: "/layer/create/"+mean.id}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            viewStateVal('layers-dao', 'layers')[mean.id][data.id] = data
            errorCatcherForAsync(()=>fireEvent('layers-dao', 'layer-created', [data]))
          });
})

registerEvent('layers-dao', 'layer-created', (stateSetter, layer)=>[layer])

registerEvent('layers-dao', 'create-candidate', (stateSetter, mean)=>{
  if(viewStateVal('layers-dao', 'layers-candidates')==null){
    stateSetter('layers-candidates', {})
    viewStateVal('layers-dao', 'layers-candidates')[mean.id] = 0
  }
  const layersCandidates = viewStateVal('layers-dao', 'layers-candidates')
  layersCandidates[mean.id] = layersCandidates[mean.id]++
})

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

const errorCatcherForAsync = function(callback){
  try{
    callback()
  } catch (e){
    console.error(e)
  }
}
