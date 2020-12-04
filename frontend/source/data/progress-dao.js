import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'progress'

const getByTaskEvent = 'get-by-task'
const gotByTaskEvent = 'got-by-task'
const getByTaskUrlOffest = '/get/for/task'

registerEvent(DataConstants.progressRep, getByTaskEvent, (stSetter, task)=>{
  sendGet('/' + repName + getByTaskUrlOffest + '/' + task.id, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data

      var objMap = chkSt(DataConstants.progressRep, DataConstants.objMap)
      if(objMap == null){
        objMap = {}
      }
      objMap[task.id] = progress
      stSetter(DataConstants.objMap, objMap)
      fireEvent(DataConstants.progressRep, gotByTaskEvent, [progress])
  })
})
registerEvent(DataConstants.progressRep, gotByTaskEvent, (stSetter, progress)=>progress)
