//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'

const NAME = 'realm'
const REP_NAME = NAME + '-rep'
const CURRENT_REALM = 'currentRealm'

export const createRealmRep = function(){
  createRep(NAME, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    setCurrent(stSetter, arg)
  }
}

const setCurrent = function(stSetter, objMap){
  for (const id in objMap) {
    if (objMap.hasOwnProperty(id) && objMap[id].current) {
      stSetter(CURRENT_REALM, objMap[id])
    }
  }
}

registerEvent(REP_NAME, 'change-current-realm', function(stateSetter, realm){
  sendPost('/realms/current/'+realm.id, null, ()=>{})
  const realmsMap = chkSt(REP_NAME, 'objects')

  for(const realmId in realmsMap){
    realmsMap[realmId].current = false
  }

  realm.current = true
  stateSetter(CURRENT_REALM, realm)
})
