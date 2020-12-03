//import $ from 'jquery'

//HashMap year -> array of weeks
var weeks = {}
var weeksList = {}
var weeksLoaded = false

export var CreateWeek = function(id, title, task){
  return {
    'id': id,
    'title': title,
    'task': task
  }
};

var saveTasksInList = function(){
  for(var i in weeks){
    for(var j in weeks[weeks[i]]){
      weeksList[""+weeks[weeks[i]][j].id] = weeks[weeks[i]][j]
    }
  }
}

export var AllWeeks = function(callback){
  if(!weeksLoaded){
      $.ajax({url: "/week/all"}).then(function(data) {
                var receivedData = typeof data == 'string'? JSON.parse(data): data
                weeks = receivedData
                saveTasksInList()
                if(callback != null){
                  callback()
                }
                weeksLoaded = true
              });
  }
  return weeks
}

export var WeekById = function(id){
  return weeksList[id]
}
