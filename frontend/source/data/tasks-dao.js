import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('tasks-dao', 'add-task', (stateSetter, subject)=>{
  if(subject.tasks==null){
    subject.tasks = []
  }
  const task = {
    position: getMaxTaskPosition(subject.tasks)+1
  }
  subject.tasks[task.position] = task
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
