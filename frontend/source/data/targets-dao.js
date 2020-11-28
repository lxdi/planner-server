//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'

const name = 'target'
const repName = name + '-rep'
const indexByRealmid = 'index-by-realmid'
const objMapName = 'objects'

export const createTargetRep = function(){
  createRep(name, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    stSetter(indexByRealmid, createIndexByRealmid(arg))
  }
  if(spanName == 'creationSpan'){
    updateIndexByRealmid(arg, chkSt(repName, indexByRealmid))
    addNextIdToLast(arg.parentid, arg.id, arg.realmid)
  }
  if(spanName == 'deleteSpan'){
    stSetter(objMapName, null)
    stSetter(indexByRealmid, null)
    // const targetsObjMap = chkSt(repName, objMapName)
    // removeObjById(arg, targetsObjMap)
    // stSetter(indexByRealmid, createIndexByRealmid(targetsObjMap))
  }
}

const createIndexByRealmid = function(targets){
  const index = {}
  for(const id in targets){
    const target = targets[id]
    updateIndexByRealmid(target, index)
  }
  return index
}

const updateIndexByRealmid = function(target, index){
  if(index[target.realmid]==null){
    index[target.realmid] = {}
  }
  index[target.realmid][target.id] = target
}

const addNextIdToLast = function(parentId, idOfNext, realmid){
  const targets = chkSt(repName, indexByRealmid)[realmid]
  for(const id in targets){
    if(id != idOfNext && targets[id].parentid == parentId && targets[id].nextid == null){
      targets[id].nextid = idOfNext
    }
  }
}

const removeObjById = function(obj, objmap){
  for(const id in objmap){
    if(objmap[id].nextid == obj.id){
      objmap[id].nextid = obj.nextid
    }
  }
}
