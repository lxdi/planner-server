import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'
import {DataConstants} from './data-constants'

const createUrlOffset = '/external/task/create'
const updateUrlOffset = '/external/task/update'

registerEvent(DataConstants.externalTasksRep, 'create', (stateSetter, task)=>{
  sendPut(createUrlOffset, task, (data)=>{
      fireEvent(DataConstants.externalTasksRep, 'created')
  })
})

registerEvent(DataConstants.externalTasksRep, 'created', ()=>{})

registerEvent(DataConstants.externalTasksRep, 'update', (stateSetter, task)=>{
  sendPost(updateUrlOffset, task, (data)=>{
      fireEvent(DataConstants.externalTasksRep, 'updated')
  })
})

registerEvent(DataConstants.externalTasksRep, 'updated', ()=>{})
