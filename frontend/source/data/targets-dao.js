//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const NAME = 'target'
const REP_NAME = NAME + '-rep'
const INDEX_BY_REALM = 'index-by-realmid'
const REALM_ID_FEILD_NAME = 'realmid'

export const createTargetRep = function(){
  createRep('target-rep', '/targets', callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    var targetsMap = chkSt(REP_NAME, 'objects')
    stSetter(INDEX_BY_REALM, createIndex(targetsMap, REALM_ID_FEILD_NAME))
  }

  if(spanName == 'creationSpan'){
    updateIndex(arg, chkSt(REP_NAME, INDEX_BY_REALM), REALM_ID_FEILD_NAME)
    addNextIdToLast(arg.parentid, arg.id, arg.realmid)
  }

  if(spanName == 'deleteSpan'){
    stSetter('objects', null)
    stSetter(INDEX_BY_REALM, null)
  }

  if(spanName == 'updateSpan'){
    const targetsObjMap = chkSt(REP_NAME, 'objects')
    stSetter(INDEX_BY_REALM, createIndex(targetsObjMap, REALM_ID_FEILD_NAME))
  }
}

const addNextIdToLast = function(parentId, idOfNext, realmid){
  const targets = chkSt(REP_NAME, INDEX_BY_REALM)[realmid]
  for(const id in targets){
    if(id != idOfNext && targets[id].parentid == parentId && targets[id].nextid == null){
      targets[id].nextid = idOfNext
    }
  }
}
