
const debugMode = {
  on: false,
  //on: true,
  depth: ['stateSetter', 'event', 'reaction', 'reactionComb'] // viewStateVal, registerObject
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
    reactionsComb:[],
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

export const registerReactionCombo = function(objName, combInit, reaction){
  if(objects[objName]==null){
    registerObject(objName)
  }
  objects[objName].reactionsComb.push(new createCombination(combInit, reaction))
}

function createCombination(combInit, reaction){
  for(var objName in combInit){
    this[objName] = {}
    this[objName][combInit[objName]] = false
  }
  this['reactionComb'] = reaction
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
  fireReactions(objName, evName, product)
  fireComboReactions(objName, evName)
  releaseFromCycle(objName, evName)
}

const fireReactions = function(objName, evName, product){
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
}

const fireComboReactions = function(objName, evName){
  for(var reactObjName in objects){
    for(var reactCombId in objects[reactObjName].reactionsComb){
      const reactComb = objects[reactObjName].reactionsComb[reactCombId]
      if(reactComb[objName]!=null && reactComb[objName][evName]!=null){
        reactComb[objName][evName] = true
        if(checkReactComb(reactComb)){
          debugConsole({event:'reactionComb', reactObjName: reactObjName, objName:objName, evName: evName, reactComb: reactComb})
          reactComb['reactionComb'](stateSetter.bind(null, reactObjName))
          reactCombReset(reactComb)
        }
      }
    }
  }
}

const checkReactComb = function(reactComb){
  var check = true
  for(var objNameToReact in reactComb){
    if(objNameToReact != 'reactionComb'){
      for(var evName in reactComb[objNameToReact]){
        if(reactComb[objNameToReact][evName]==false){
          check = false
        }
      }
    }
  }
  return check
}

const reactCombReset = function(reactComb){
  for(var objNameToReact in reactComb){
    if(objNameToReact != 'reactionComb'){
      for(var evName in reactComb[objNameToReact]){
        reactComb[objNameToReact][evName] = false
      }
    }
  }
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
