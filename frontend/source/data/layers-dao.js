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

registerEvent('layers-dao', 'create', (layer)=>{})

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
