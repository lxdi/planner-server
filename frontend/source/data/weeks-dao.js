import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'week'

const getCurrentEvent = 'get-current-list'
const gotCurrentEvent = 'got-current-list'
const getCurrentUrlOffest = '/get/all/current/year'

registerEvent(DataConstants.weekRep, getCurrentEvent, (stSetter)=>{
  sendGet('/' + repName + getCurrentUrlOffest, (data)=>{
      var list = typeof data == 'string'? JSON.parse(data): data
      stSetter(DataConstants.objList, list)
      fireEvent(DataConstants.weekRep, gotCurrentEvent, [list])
  })
})
registerEvent(DataConstants.weekRep, gotCurrentEvent, (stSetter, list)=>list)
