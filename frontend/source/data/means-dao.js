//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep, basicListReceiving} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const NAME = 'mean'
const REP_NAME = NAME + '-rep'
const INDEX_BY_REALM = 'index-by-realmid'
const INDEX_WITH_PRIORITIES = 'index-with-priorites'
const GET_LIST_WITH_PRIORITY_SPAN = 'getWithPrioritySpan'

export const createMeanRep = function(){
  createRep('mean-rep','/means', callback)

  basicListReceiving('mean-rep', '/means', '/list', 'get-with-priority', 'got-with-priority',
    GET_LIST_WITH_PRIORITY_SPAN, {'with-priorities': true}, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    const meansObjMap = chkSt(REP_NAME, 'objects')
    stSetter(INDEX_BY_REALM, createIndex(meansObjMap, 'realmid'))
  }
  if(spanName == 'creationSpan'){
    updateIndex(arg, chkSt(REP_NAME, INDEX_BY_REALM), 'realmid')
    addNextIdToLast(arg.parentid, arg.id, arg.realmid)
  }
  if(spanName == 'deleteSpan'){
    stSetter('objects', null)
    stSetter(INDEX_BY_REALM, null)
    stSetter(INDEX_WITH_PRIORITIES, null)
  }
  if(spanName == 'updateSpan'){
    const meansObjMap = chkSt(REP_NAME, 'objects')
    stSetter(INDEX_BY_REALM, createIndex(meansObjMap, 'realmid'))
  }
  if(spanName == GET_LIST_WITH_PRIORITY_SPAN){
    stSetter(INDEX_WITH_PRIORITIES, arg)
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
