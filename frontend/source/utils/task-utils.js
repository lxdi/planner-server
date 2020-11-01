
export const filterOutMemoTask = function(tasks){
  var result = []
  tasks
    .filter(task => !isMemoTask(task))
    .forEach(task => result.push(task))
  return result
}

export const getOnlyMemoOnDateTasks = function(tasks, dateString){
  var result = []
  tasks
    .filter(task => isMemoTask(task))
    .filter(task => task.repetition!=null && task.repetition.planDate == dateString)
    .forEach(task => result.push(task))
  return result
}

const isMemoTask = function(task){
  return task.repetition != null && task.repetition.day_rep
}
