
export const isValidMean = function(alerts, mean){
  var result = true
  if(mean.title == null || mean.title==''){
    alerts.push('Title is not valid')
    result = false
  }
  if(mean.layers!=null && mean.layers.length>0){
    for(var i in mean.layers){
      if(!isLayerValid(alerts, mean.layers[i])){
        result = false
      }
    }
  }
  return result
}

const isLayerValid = function(alerts, layer){
  var tasksCount = 0
  if(layer.subjects!=null && layer.subjects.length>0){
      for(var i in layer.subjects){
        if(layer.subjects[i].tasks!=null && layer.subjects[i].tasks.length>0){
          for(var j in layer.subjects[i].tasks){
            if(layer.subjects[i].tasks[j]!=null){
              tasksCount++
            }
          }
        }
      }
  }
  if(tasksCount>12){
    alerts.push('Layer ' + layer.priority + ' has more than 12 tasks')
    return false
  }
  return true
}
