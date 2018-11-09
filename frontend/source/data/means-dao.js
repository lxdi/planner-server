import {AllTargets} from './targets-dao'
import $ from 'jquery'

var meansLoaded = false
const means = {}
means.__proto__ = {
  map: function(callback, filter){
    var result = []
    for (var i in this){
      if(i != 'map'){
        if(filter!=null){
          if(filter(this[i])){
            result.push(callback(this[i]))
          }
        } else {
          result.push(callback(this[i]))
        }
      }
    }
    return result
  }
}

const protomean = {
  targetsString: function(){
    var targetsString = '';
    var divisor = ' #';
    for(var i in this.targets){
      targetsString = targetsString +divisor+ this.targets[i].toString();
    }
    return targetsString
  },
  toString: function(){
    return this.title + ' ' + this.targetsString();
  }
}

const importMeansDto = function(meansDto){
  for(var i in meansDto){
    means[""+meansDto[i].id] = meansDto[i]
  }
  ResolveMeans();
}

export var ResolveMeans = function(){
  for(var i in means){
    if(means.hasOwnProperty(i)){
      resolveMean(means[i])
    }
  }
}

const resolveMean = function(mean){
  mean.children = []
  mean.targets = []
  mean.__proto__ = protomean
  for(var j in means){
    if(means[j].parentid == mean.id){
      mean.children.push(means[j])
    }
  }
  var alltargets = AllTargets()
  for(var tid in mean.targetsIds){
      var target = alltargets[mean.targetsIds[tid]]
      if(target!=null){
        mean.targets.push(target)
      }
  }
}

export var AllMeans = function(callback){
  if(!meansLoaded){
      $.ajax({url: "/mean/all/lazy"}).then(function(data) {
                var receivedData = typeof data == 'string'? JSON.parse(data): data
                importMeansDto(receivedData)
                if(callback != null)
                  callback()
              });
    meansLoaded = true
  }
  return means
}

export var CreateMean = function(id, title, targets, children){
  var childrenToAdd = children!=null? children: []
  var newMean = {
    'id': id,
    'title': title,
    'targets': targets,
    'children': childrenToAdd,
  }
  newMean.__proto__ = protomean
  return newMean;
}

export var AddMean = function(mean, parent, callback){
  mean.parentid = parent!=null? parent.id: null
  mean.targetsIds = []
  for(var i in mean.targets){
    mean.targetsIds.push(mean.targets[i].id)
  }
  $.ajax({
    url: '/mean/create',
    type: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(mean),
    success: function(data) {
      means[""+data.id] = data
      ResolveMeans()
      callback()
    }
  });
}

export var DeleteMeanById = function(id, callback){
  $.ajax({
    url: '/mean/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete means[id]
      ResolveMeans()
      callback()
    }
  });
}

// Remove mean that has only one target and that target has id = targetid
// Removing is only in UI because on server-side mean is removed automatically when target is removed
export var DeleteMeanWithTarget = function(targetid, callback){
  for(var i in means){
    if(means.hasOwnProperty(i)){
      if(means[i].targets.length == 1 && means[i].targets[0].id == targetid){
        //delete means[i]
        deleteMeanUI(means[i])
      } else {
        if(means[i].targets.length>1){
          for(var j in means[i].targets){
            if(means[i].targets[j].id == targetid){
              delete means[i].targets[j]
            }
          }
        }
      }
    }
  }
  ResolveMeans()
  if(callback!=null)
    callback()
}

//delete Mean only form UI
var deleteMeanUI = function(mean){
  delete means[mean.id]
  var parent = means[mean.parentid]
  if(parent != null){
    parent.children = parent.children.filter(function(e){
      e.id!=mean.id
    })
  }
}

export var UpdateMean = function(mean, callback){
  mean.targetsIds = []
  for(var i in mean.targets){
    mean.targetsIds.push(mean.targets[i].id)
  }
  $.ajax({
    url: '/mean/update',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(mean),
    success: function(data) {
      means[""+data.id] = data
      ResolveMeans()
      callback()
    }
  });
}

export var MeanById = function(id){
  return means[id];
}
