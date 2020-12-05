import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

import {DataConstants} from './data-constants'

const repName = 'progress'

const getByTaskEvent = 'get-by-task'
const gotByTaskEvent = 'got-by-task'
const getByTaskUrlOffest = '/get/for/task'

const finishTaskEvent = 'finish-task'
const finishedByTaskEvent = 'got-by-task'
const finishTaskUrlOffest = '/finish/task'

const finishTaskEventSP = 'finish-task-sp'
const finishedByTaskEventSP = 'got-by-task'
const finishTaskUrlOffestSP = '/finish/task'
const finishTaskUrlOffestSP2 = '/with/sp'

registerEvent(DataConstants.progressRep, getByTaskEvent, (stSetter, task)=>{
  sendGet('/' + repName + getByTaskUrlOffest + '/' + task.id, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(DataConstants.progressRep, gotByTaskEvent, [progress])
  })
})
registerEvent(DataConstants.progressRep, gotByTaskEvent, (stSetter, progress)=>progress)

registerEvent(DataConstants.progressRep, finishTaskEvent, (stSetter, task)=>{
  sendPost('/' + repName + finishTaskEvent + '/' + task.id, null, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(DataConstants.progressRep, finishedByTaskEvent, [progress])
  })
})
registerEvent(DataConstants.progressRep, finishedByTaskEvent, (stSetter, progress)=>progress)

registerEvent(DataConstants.progressRep, finishTaskEventSP, (stSetter, task, repPlan)=>{
  sendPost('/' + repName + finishTaskUrlOffestSP + '/' + task.id + finishTaskUrlOffestSP2 +'/'+repPlan.id, null, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(DataConstants.progressRep, finishedByTaskEventSP, [progress])
  })
})
registerEvent(DataConstants.progressRep, finishedByTaskEventSP, (stSetter, progress)=>progress)


const updateProgress = function(progress, task, stSetter){
  var objMap = chkSt(DataConstants.progressRep, DataConstants.objMap)
  if(objMap == null){
    objMap = {}
  }
  objMap[task.id] = progress
  stSetter(DataConstants.objMap, objMap)
}
