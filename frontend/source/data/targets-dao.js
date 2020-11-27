//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'

const name = 'target'
const repName = name + '-rep'
const indexByRealmid = 'index-by-realmid'

export const createTargetRep = function(){
  createRep(name, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    stSetter(indexByRealmid, createIndexByRealmid(arg))
  }
  if(spanName == 'creationSpan'){
    updateIndexByRealmid(arg, chkSt(repName, indexByRealmid))
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
