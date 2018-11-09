import $ from 'jquery'
import {AllMeans, MeanById} from './means-dao'
import {AllWeeks, WeekById} from './weeks-dao'

var tasksLoaded = false;
const tasks = {}

var improtTasksFromDto = function(tasksDtos){
  for(var i in tasksDtos){
    tasks[""+tasksDtos[i].id] = tasksDtos[i]
  }
}

var resolveTasks = function(){
  for(var i in tasks){
    if(tasks[i].meanid!=null){
      tasks[i].mean = MeanById(tasks[i].meanid)
    }
    if(tasks[i].weekid!=null){
      tasks[i].week = WeekById(tasks[i].weekid)
    }
  }
}

export var CreateTask = function(id, title, mean){
  return {
    'id': id,
    'title': title,
    'mean': mean,
    toString: function(){
      return this.title + ' #' + this.mean!=null? this.mean.title:'';
    }
  }
}

export var AllTasks = function(callback){
  if(!tasksLoaded){
      $.ajax({url: "/task/all"}).then(function(data) {
                var receivedData = typeof data == 'string'? JSON.parse(data): data
                improtTasksFromDto(receivedData)
                resolveTasks()
                if(callback != null){
                  callback()
                }
                tasksLoaded = true
              });
  }
  return tasks;
}

export var TaskWithWeek = function(weekid){
  for(var i in tasks){
    if(tasks[i].weekid == weekid)
      return tasks[i]
  }
}

export var AddTask = function(task, week, callback){
  if(week!=null)
    task.weekid = week.id
  if(task.mean!=null)
    task.meanid = task.mean.id
  $.ajax({
    url: '/task/create',
    type: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(task),
    success: function(data) {
      tasks[""+data.id] = data
      resolveTasks()
      if(callback!=null)
        callback()
    }
  });
}

export var DeleteTaskById = function(id, callback){
  $.ajax({
    url: '/task/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete tasks[id]
      resolveTasks()
      if(callback!=null)
        callback()
    }
  });
}

export var UpdateTask = function(task, callback){
  if(task.mean!=null)
    task.meanid = task.mean.id
  if(task.week!=null)
    task.weekid = task.week.id
  $.ajax({
    url: '/task/update',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(task),
    success: function(data) {
      tasks[""+data.id] = data
      resolveTasks()
      if(callback!=null)
        callback()
    }
  });
}
