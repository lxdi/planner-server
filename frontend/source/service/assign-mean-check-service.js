
export const isCheckedTask = function(dto, task){
  if(dto.layers == null){
    return false
  }

  var result = false
  dto.layers.forEach(layerDto => {
    if(layerDto.taskIds!=null && layerDto.taskIds.includes(task.id)){
      result = true
    }
  })
  return result
}

export const checkTask = function(comp, dto, layer, task){
  if(dto.layers == null){
    dto.layers = []
  }

  const isAdded = dto.layers.filter(l => {
    if(l.layerId == layer.id){
      if(l.taskIds.includes(task.id)){
        const idx = l.taskIds.indexOf(task.id)
        l.taskIds.splice(idx, 1)
        return true
      }
      l.taskIds.push(task.id)
      return true
    }
    return false
  }).length > 0

  if(!isAdded){
    dto.layers.push({layerId: layer.id, taskIds: [task.id]})
  }
  comp.setState({})
  return isCheckedTask(dto, task)
}

export const isLayerChecked = function(dto, layer){
  if(layer.tasks == null){
    return false
  }

  for(var i = 0; i<layer.tasks.length; i++){
    if(!isCheckedTask(dto, layer.tasks[i])){
      return false
    }
  }
  return true
}

export const checkLayer = function(comp, dto, layer){
  if(layer.tasks!=null){
    layer.tasks.forEach(task => checkTask(comp, dto, layer, task))
  }
  comp.setState({})
  return isLayerChecked(dto, layer)
}
