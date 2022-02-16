import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'

const NAME = 'progress'
const REP_NAME = NAME + '-rep'

registerEvent(REP_NAME, 'get-by-task', (stSetter, task)=>{

  sendGet('/' + NAME + '?task-id=' + task.id, (data)=>{

      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(REP_NAME, 'got-by-task', [progress])
  })
})
registerEvent(REP_NAME, 'got-by-task', (stSetter, progress)=>progress)

registerEvent(REP_NAME, 'finish-task', (stSetter, task)=>{
  sendPost('/' + NAME + '/finished?task-id=' + task.id, null, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(REP_NAME, 'got-by-task', [progress])
  })
})

registerEvent(REP_NAME, 'finish-task-sp', (stSetter, task, repPlan)=>{
  sendPost('/' + NAME + 'finished?task-id='+ task.id + '&plan-id='+repPlan.id, null, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(REP_NAME, 'got-by-task', [progress])
  })
})

registerEvent(REP_NAME, 'finish-rep', (stSetter, rep, task)=>{
  sendPost('/' + NAME + 'finished?rep-id=' + rep.id, null, (data)=>{
      const progress = typeof data == 'string'? JSON.parse(data): data
      updateProgress(progress, task, stSetter)
      fireEvent(REP_NAME, 'got-by-task', [progress])
  })
})

registerEvent(REP_NAME, 'get-actual', (stSetter)=>{
  sendGet('/' + NAME + '/actual', (data)=>{
      const actual = typeof data == 'string'? JSON.parse(data): data
      stSetter('actual', actual)
      fireEvent(REP_NAME, 'got-actual', [actual])
  })
})
registerEvent(REP_NAME, 'got-actual', (stSetter, actual)=>actual)

registerEvent(REP_NAME, 'delete-unfinished-reps', (stSetter, task)=>{
  sendDelete('/' + NAME + '/unfinished/repetitions?task-id='+task.id, ()=>{
    updateProgress(null, task, stSetter)
    fireEvent(REP_NAME, 'deleted-unfinished-reps')
  })
})
registerEvent(REP_NAME, 'deleted-unfinished-reps', (stSetter)=>{})

const updateProgress = function(progress, task, stSetter){
  var objMap = chkSt(REP_NAME, 'objects')
  if(objMap == null){
    objMap = {}
  }
  objMap[task.id] = progress
  stSetter('objects', objMap)
}
