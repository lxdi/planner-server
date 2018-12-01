import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

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

registerEvent('tasks-dao', 'add-task-to-drag', (stateSetter, subject, task)=>stateSetter('draggable-task', {subject: subject, task: task}))
registerEvent('tasks-dao', 'release-draggable-task', (stateSetter)=>stateSetter('draggable-task', null))
registerEvent('tasks-dao', 'move-task', (stateSetter, targetSubject, targetTask)=>{
  const sourceSubject = viewStateVal('tasks-dao', 'draggable-task').subject
  const sourceTask = viewStateVal('tasks-dao', 'draggable-task').task
  if(targetTask!=null){
    if(sourceTask!=targetTask){
      if(sourceSubject!=targetSubject){
        deleteTask(sourceSubject, sourceTask)
        insertTask(targetSubject, sourceTask, targetTask.position)
        stateSetter('draggable-task', {subject: targetSubject, task: sourceTask})
      } else {
        swapTasks(targetSubject, targetTask.position, sourceTask.position)
      }
    }
  } else {
    if(sourceSubject!=targetSubject){
      deleteTask(sourceSubject, sourceTask)
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

const insertTask = function(targetSubject, sourceTask, targetPosition){
    const newArrayOfTasks = []
    for(var taskPos in targetSubject.tasks){
      const task = targetSubject.tasks[taskPos]
      const currentTaskPos = task.position
      if(currentTaskPos>=targetPosition){
        task.position = task.position+1
        if(currentTaskPos==targetPosition){
          sourceTask.position = targetPosition
          newArrayOfTasks[sourceTask.position] = sourceTask
        }
      }
      newArrayOfTasks[task.position] = task
    }
    targetSubject.tasks = newArrayOfTasks
}

const deleteTask = function(subject, taskToDelete){
  const newArrayOfTasks = []
  for(var taskPos in subject.tasks){
    const task = subject.tasks[taskPos]
    const currentTaskPos = task.position
    if(currentTaskPos>taskToDelete.position){
      task.position = currentTaskPos-1
    }
    if(task!=taskToDelete){
      newArrayOfTasks[task.position] = task
    }
  }
  subject.tasks = newArrayOfTasks
}

const swapTasks = function(subject, pos1, pos2){
  const tempTask = subject.tasks[pos1]
  subject.tasks[pos1] = subject.tasks[pos2]
  subject.tasks[pos1].position = pos1
  subject.tasks[pos2] = tempTask
  subject.tasks[pos2].position = pos2
}

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
