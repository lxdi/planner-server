import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'
import {DataConstants} from './data-constants'

const URL = '/external-task'

registerEvent(DataConstants.externalTasksRep, 'create', (stateSetter, task)=>{
  sendPut(URL, task, (data)=>{
      fireEvent(DataConstants.externalTasksRep, 'created')
  })
})

registerEvent(DataConstants.externalTasksRep, 'created', ()=>{})

registerEvent(DataConstants.externalTasksRep, 'update', (stateSetter, task)=>{
  sendPost(URL, task, (data)=>{
      fireEvent(DataConstants.externalTasksRep, 'updated')
  })
})

registerEvent(DataConstants.externalTasksRep, 'updated', ()=>{})
