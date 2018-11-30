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
