import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'day'

const getOneDayEvent = 'get-one'
const gotOneDayEvent = 'got-one'
const getCurrentUrlOffest = '/week/get/day'
const cleanAll = 'clean-all'

registerEvent(DataConstants.dayRep, getOneDayEvent, (stSetter, day)=>{
  sendGet(getCurrentUrlOffest + '/' + day.id, (data)=>{
      const dayMt = typeof data == 'string'? JSON.parse(data): data
      Object.assign(day, dayMt)
      day.isFull = true

      var objmap = chkSt(DataConstants.dayRep, DataConstants.objMap)
      if(objmap == null){
        objmap = {}
        stSetter(DataConstants.objMap, objmap)
      }
      objmap[day.id] = day
      fireEvent(DataConstants.dayRep, gotOneDayEvent, [day])
  })
})
registerEvent(DataConstants.dayRep, gotOneDayEvent, (stSetter, day)=>day)

registerEvent(DataConstants.dayRep, cleanAll, (stSetter)=>{
  stSetter(DataConstants.objMap, null)
})
