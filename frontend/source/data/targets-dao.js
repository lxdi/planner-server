//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const name = 'target'
const repName = name + '-rep'
const indexByRealmid = 'index-by-realmid'
const objMapName = 'objects'
const realmIdFieldName = 'realmid'

export const createTargetRep = function(){
  createRep(name, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    stSetter(indexByRealmid, createIndex(arg, realmIdFieldName))
  }
  if(spanName == 'creationSpan'){
    updateIndex(arg, chkSt(repName, indexByRealmid), realmIdFieldName)
    addNextIdToLast(arg.parentid, arg.id, arg.realmid)
  }
  if(spanName == 'deleteSpan'){
    stSetter(objMapName, null)
    stSetter(indexByRealmid, null)
  }
  if(spanName == 'updateSpan'){
    const targetsObjMap = chkSt(repName, objMapName)
    stSetter(indexByRealmid, createIndex(targetsObjMap, realmIdFieldName))
  }
}

const addNextIdToLast = function(parentId, idOfNext, realmid){
  const targets = chkSt(repName, indexByRealmid)[realmid]
  for(const id in targets){
    if(id != idOfNext && targets[id].parentid == parentId && targets[id].nextid == null){
      targets[id].nextid = idOfNext
    }
  }
}
