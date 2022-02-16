//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const NAME = 'mean'
const REP_NAME = name + '-rep'
const INDEX_BY_REALM = 'index-by-realmid'

export const createMeanRep = function(){
  createRep(NAME, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    stSetter(INDEX_BY_REALM, createIndex(arg, 'realmid'))
  }
  if(spanName == 'creationSpan'){
    updateIndex(arg, chkSt(REP_NAME, INDEX_BY_REALM), 'realmid')
    addNextIdToLast(arg.parentid, arg.id, arg.realmid)
  }
  if(spanName == 'deleteSpan'){
    stSetter('objects', null)
    stSetter(INDEX_BY_REALM, null)
  }
  if(spanName == 'updateSpan'){
    const meansObjMap = chkSt(REP_NAME, 'objects')
    stSetter(INDEX_BY_REALM, createIndex(meansObjMap, 'realmid'))
  }
}

const addNextIdToLast = function(parentId, idOfNext, realmid){
  const means = chkSt(REP_NAME, INDEX_BY_REALM)[realmid]

  for(const id in means){
    if(id != idOfNext && means[id].parentid == parentId && means[id].nextid == null){
      means[id].nextid = idOfNext
    }
  }
}

registerEvent(REP_NAME, 'reposition', (stSetter, meansArr) => {
  sendPost('/means/reposition/list', meansArr, () => {
    fireEvent(REP_NAME, 'repositioned')
  })
})
registerEvent(REP_NAME, 'repositioned', ()=>{})

registerEvent('testing-rep', 'reposition', (stSetter, testingsArr) => {
  sendPost('/task-testings/reposition/list', testingsArr, () => {
    fireEvent('testing-rep', 'repositioned')
  })
})
registerEvent('testing-rep', 'repositioned', ()=>{})
