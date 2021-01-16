import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'week'

const getCurrentEvent = 'get-current-list'
const gotCurrentEvent = 'got-current-list'
const getCurrentUrlOffest = '/get/all/current/year'

const getPrevEvent = 'get-prev'
const gotPrevEvent = 'got-prev'
const getPrevUrlOffest = '/get/prev'

const getNextEvent = 'get-next'
const gotNextEvent = 'got-next'
const getNextUrlOffest = '/get/next'

const movePlansEvent = 'move-plans'
const movedPlansEvent = 'moved-plans'
const movingPlansUrlOffest = '/move/plans'

const cleanAll = 'clean-all'

registerEvent(DataConstants.weekRep, getCurrentEvent, (stSetter)=>{
  sendGet('/' + repName + getCurrentUrlOffest, (data)=>{
      const list = typeof data == 'string'? JSON.parse(data): data
      stSetter(DataConstants.objList, list)

      const objMap = {}
      list.forEach(week => {
        objMap[week.id] = week
      })
      stSetter(DataConstants.objMap, objMap)
      fireEvent(DataConstants.weekRep, gotCurrentEvent, [list])
  })
})
registerEvent(DataConstants.weekRep, gotCurrentEvent, (stSetter, list)=>list)

registerEvent(DataConstants.weekRep, getPrevEvent, (stSetter, current)=>{
  sendGet('/' + repName + getPrevUrlOffest+'/'+current.id, (data)=>{
      const week = typeof data == 'string'? JSON.parse(data): data
      chkSt(DataConstants.weekRep, DataConstants.objMap)[week.id] = week
      chkSt(DataConstants.weekRep, DataConstants.objList).unshift(week)
      fireEvent(DataConstants.weekRep, gotPrevEvent, [week])
  })
})
registerEvent(DataConstants.weekRep, gotPrevEvent, (stSetter, week)=>week)

registerEvent(DataConstants.weekRep, getNextEvent, (stSetter, current)=>{
  sendGet('/' + repName + getNextUrlOffest+'/'+current.id, (data)=>{
      const week = typeof data == 'string'? JSON.parse(data): data
      chkSt(DataConstants.weekRep, DataConstants.objMap)[week.id] = week
      chkSt(DataConstants.weekRep, DataConstants.objList).push(week)
      fireEvent(DataConstants.weekRep, gotNextEvent, [week])
  })
})
registerEvent(DataConstants.weekRep, gotNextEvent, (stSetter, week)=>week)

registerEvent(DataConstants.weekRep, movePlansEvent, (stSetter, movingDto)=>{
  sendPost('/' + repName + movingPlansUrlOffest, movingDto, ()=>{
      cleanRep(stSetter)
      fireEvent(DataConstants.weekRep, movedPlansEvent)
  })
})
registerEvent(DataConstants.weekRep, movedPlansEvent, (stSetter)=>{})

registerEvent(DataConstants.weekRep, cleanAll, (stSetter)=>cleanRep(stSetter))

const cleanRep = function(stSetter){
  stSetter(DataConstants.objMap, null)
  stSetter(DataConstants.objList, null)
}
