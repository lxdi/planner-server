
const debugMode = {
  on: false,
  depth: ['stateSetter', 'event', 'reaction'] // viewStateVal, registerObject
}
const debugConsole = function(msg){
  if(debugMode.on){
    if(debugMode.depth.indexOf(msg.event)>=0){
      console.log(msg)
    }
  }
}

const objects = []
var cycleBlockers = []

export const registerObject = function(objName, initState){
  debugConsole({event:'registerObject', objName: objName, initState: initState})
  objects[objName] = {
    name: objName,
    events: [],
    reactions: [],
    state:initState!=null?initState:{}
  }
  return objects[objName]
}

export const viewStateVal = function(objName, valName){
  if(objects[objName]==null){
    throw new Exception('No such object', objName, 'viewStateVal')
  }
  debugConsole({event:'viewStateVal', objName: objName, valName: valName})
  return objects[objName].state[valName]
}

const stateSetter = function(objName, valName, value){
  debugConsole({event:'stateSetter', objName:objName, valName: valName, value: value})
  objects[objName].state[valName] = value
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
  validation(objName, evName)
  if(args!=null){
    //args.unshift(objects[objName].state)
    args.unshift(stateSetter.bind(null, objName))
  } else {
    args = [stateSetter.bind(null, objName)]
  }
  debugConsole({event:'event', objName:objName, evName: evName, args: args})
  const product = objects[objName].events[evName].apply(null, args)
  for(var reactObjName in objects){
    if(reactObjName!=objName && objects[reactObjName].reactions[objName]!=null){
      // if(objects[reactObjName].reactions[objName]['any']!=null){
      //   objects[reactObjName].reactions[objName]['any'](product)
      // }
      if(objects[reactObjName].reactions[objName][evName]!=null){
        debugConsole({event:'reaction', reactObjName: reactObjName, objName:objName, evName: evName, product: product})
        objects[reactObjName].reactions[objName][evName](stateSetter.bind(null, reactObjName), product)
      }
    }
  }
  releaseFromCycle(objName, evName)
}

const validation = function(objName, evName){
  if(objects[objName]==null){
    throw new Exception('No such object', objName, evName)
  }
  if(objects[objName].events[evName]==null){
    throw new Exception('No such event', objName, evName)
  }
  isCycle(objName, evName)
}

const isCycle = function(objName, evName){
  if(cycleBlockers[objName]==null){
    cycleBlockers[objName] = []
    cycleBlockers[objName].push(evName)
  } else {
    if(cycleBlockers[objName].indexOf(evName)<0){
      cycleBlockers[objName].push(evName)
    } else {
      throw new Exception('Cycle', objName, evName, cycleBlockers)
    }
  }
}

const releaseFromCycle = function(objName, evName){
  if(cycleBlockers[objName]!=null && cycleBlockers[objName].indexOf(evName)>=0){
    cycleBlockers = cycleBlockers.splice(cycleBlockers[objName].indexOf(evName), 1)
  }
}

function Exception(message, obj, ev, calls){
  this.scope = "Eventor"
  this.message = message
  this.obj = obj
  this.event = ev
  if(calls!=null)
    this.cycleBlockers = calls
}
