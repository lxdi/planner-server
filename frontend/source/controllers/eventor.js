

const objects = []

export const registerObject = function(objName){
  objects[objName] = {
    name: objName,
    events: [],
    reactions: []
  }
  return objects[objName]
}

export const registerEvent = function(objName, evName, ev){
  if(objects[objName]==null){
    registerObject(objName)
  }
  objects[objName].events[evName] = ev
}

export const registerReaction = function(objName, depObjName, depEventName, reaction){
  if(objects[objName]==null){
    registerObject(objName)
  }
  if(Array.isArray(depEventName)){
    for(var i in depEventName){
      registerReaction(objName, depObjName, depEventName[i], reaction)
    }
  } else {
    if(objects[objName].reactions[depObjName]==null){
      objects[objName].reactions[depObjName] = []
    }
    objects[objName].reactions[depObjName][depEventName] = reaction
  }
}

export const fireEvent = function(objName, evName, args){
  if(objects[objName]==null){
    throw new Exception('No such object', objName, evName)
  }
  if(objects[objName].events[evName]==null){
    throw new Exception('No such event', objName, evName)
  }
  const product = objects[objName].events[evName].apply(null, args)
  for(var reactObjName in objects){
    if(reactObjName!=objName && objects[reactObjName].reactions[objName]!=null){
      // if(objects[reactObjName].reactions[objName]['any']!=null){
      //   objects[reactObjName].reactions[objName]['any'](product)
      // }
      if(objects[reactObjName].reactions[objName][evName]!=null){
        objects[reactObjName].reactions[objName][evName](product)
      }
    }
  }
}

function Exception(message, obj, ev){
  this.scope = "Eventor"
  this.message = message
  this.obj = obj
  this.event = ev
}
