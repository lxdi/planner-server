import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {insertObj, deleteObj, swapObjs, makeMap} from 'js-utils'
import {sendDelete, sendPost, sendGet} from './postoffice'

registerEvent('tasks-dao', 'add-task', (stateSetter, subject, task)=>{
  if(subject.tasks==null){
    subject.tasks = []
  }
  var taskToCreate = null
  if(task==null){
    taskToCreate = {position: getMaxTaskPosition(subject.tasks)+1}
  } else {
    taskToCreate = task
    taskToCreate.position = getMaxTaskPosition(subject.tasks)+1
  }
  subject.tasks[taskToCreate.position] = taskToCreate
})

registerEvent('tasks-dao', 'delete-task', (stateSetter, subject, task)=>{
  sendDelete('/task/delete/'+task.id, (data)=>{
    for(var i in subject.tasks){
      if(subject.tasks[i]==task){
        delete subject.tasks[i]
        break;
      }
    }
    fireEvent('tasks-dao', 'task-deleted', [data])
  })
})

registerEvent('tasks-dao', 'task-deleted', (stateSetter, task)=>task)

registerEvent('tasks-dao', 'add-task-to-drag', (stateSetter, subject, task)=>stateSetter('draggable-task', {subject: subject, task: task}))

registerEvent('tasks-dao', 'release-draggable-task', (stateSetter)=>stateSetter('draggable-task', null))

registerEvent('tasks-dao', 'move-task', (stateSetter, targetSubject, targetTask)=>{
  const sourceSubject = chkSt('tasks-dao', 'draggable-task').subject
  const sourceTask = chkSt('tasks-dao', 'draggable-task').task
  if(targetTask!=null){
    if(sourceTask!=targetTask){
      if(sourceSubject!=targetSubject){
        deleteObj(sourceSubject, sourceTask, 'tasks', 'position')
        insertObj(targetSubject, sourceTask, targetTask.position, 'tasks', 'position')
        stateSetter('draggable-task', {subject: targetSubject, task: sourceTask})
      } else {
        swapObjs(targetSubject, targetTask.position, sourceTask.position, 'tasks', 'position')
      }
    }
  } else {
    if(sourceSubject!=targetSubject){
      deleteObj(sourceSubject, sourceTask, 'tasks', 'position')
      const nextPos = getMaxTaskPosition(targetSubject.tasks)+1
      sourceTask.position = nextPos
      if(targetSubject.tasks==null){
        targetSubject.tasks = []
      }
      targetSubject.tasks[nextPos] = sourceTask
      stateSetter('draggable-task', {subject: targetSubject, task: sourceTask})
    }
  }
})

registerEvent('tasks-dao', 'finish-task', (stateSetter, task, repPlan, testings)=>{
  if(repPlan==null){
    sendPost('/task/'+task.id+'/finish', null, ()=>{
      task.finished = true
      fireEvent('tasks-dao', 'task-finished', [task])
    })
  } else {
    sendPost('/task/'+task.id+'/finish/with/repetition/'+repPlan.id, testings, ()=>{
      task.finished = true
      fireEvent('tasks-dao', 'task-finished', [task])
    })
  }
})

registerEvent('tasks-dao', 'task-finished', (task)=>task)

registerEvent('tasks-dao', 'actual-tasks-rq', (stateSetter)=>{
  sendGet('/task/get/to/repeat/all', (data)=>{
    stateSetter('actual-tasks', data)
    fireEvent('tasks-dao', 'actual-tasks-rs')
  })
})

registerEvent('tasks-dao', 'actual-tasks-rs', ()=>{})

registerEvent('tasks-dao', 'finish-repetition', (stateSetter, task, repetition)=>{
  sendPost('/task/finish/repetition/'+repetition.id, null, (data)=>{
    fireEvent('tasks-dao', 'repetition-finished', [task, repetition])
  })
})

registerEvent('tasks-dao', 'repetition-finished', (stateSetter, task, repetition)=>{
  stateSetter('actual-tasks', null)
})

registerEvent('tasks-dao', 'get-repetitions', (stSetter, task)=>{
  sendGet('task/'+task.id+'/details/repetitions', (data)=>{
    task.repetitions = makeMap(data, 'id')
    fireEvent('tasks-dao', 'got-repetitions', [task])
  })
})
registerEvent('tasks-dao', 'got-repetitions', (stSetter, task)=>task)

registerEvent('tasks-dao', 'repetitions-unfinished-remove', (stSetter, task)=>{
  sendDelete('/task/'+task.id+'/repetition/all/left/remove', ()=>{
    fireEvent('tasks-dao', 'repetitions-unfinished-removed')
  })
})
registerEvent('tasks-dao', 'repetitions-unfinished-removed', ()=>{})

const getMaxTaskPosition = function(tasks){
    var result = 0
    if(tasks!=null){
      for(var taskidx in tasks){
        if(tasks[taskidx].position>result){
          result = tasks[taskidx].position
        }
      }
    }
    return result
}