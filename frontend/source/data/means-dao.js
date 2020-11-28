//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const name = 'mean'
const repName = name + '-rep'
const realmRep = 'realm-rep'
const indexByRealmid = 'index-by-realmid'
const objMapName = 'objects'
const realmIdFieldName = 'realmid'

export const createMeanRep = function(){
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
    const meansObjMap = chkSt(repName, objMapName)
    stSetter(indexByRealmid, createIndex(meansObjMap, realmIdFieldName))
  }
}

const addNextIdToLast = function(parentId, idOfNext, realmid){
  const means = chkSt(repName, indexByRealmid)[realmid]
  for(const id in means){
    if(id != idOfNext && means[id].parentid == parentId && means[id].nextid == null){
      means[id].nextid = idOfNext
    }
  }
}
