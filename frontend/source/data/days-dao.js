import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'day'
const getCurrentUrlOffest = '/week/get/day'

registerEvent(DataConstants.dayRep, 'get-one', (stSetter, day)=>{
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
      fireEvent(DataConstants.dayRep, 'got-one', [day])
  })
})
registerEvent(DataConstants.dayRep, 'got-one', (stSetter, day)=>day)

registerEvent(DataConstants.dayRep, 'clean-all', (stSetter)=>{
  stSetter(DataConstants.objMap, null)
})
