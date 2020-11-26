//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'

const name = 'realm'
const repName = name + '-rep'
const currentRealmObjName = 'currentRealm'
const setCurrentRealmUrlOffset = '/setcurrent'

export const createRealmRep = function(){
  createRep(name, getCallback)
}

const getCallback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    setCurrent(stSetter, arg)
  }
}

const setCurrent = function(stSetter, objMap){
  for (const id in objMap) {
    if (objMap.hasOwnProperty(id) && objMap[id].current) {
      stSetter(currentRealmObjName, objMap[id])
    }
  }
}

registerEvent(repName, 'change-current-realm', function(stateSetter, realm){
  sendPost('/'+name + setCurrentRealmUrlOffset+'/'+realm.id, null, ()=>{})
  const realmsMap = chkSt(repName, 'objects')

  for(const realmId in realmsMap){
    realmsMap[realmId].current = false
  }

  realm.current = true
  stateSetter(currentRealmObjName, realm)
})
