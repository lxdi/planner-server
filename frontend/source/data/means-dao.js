import {TargetsState} from './targets-dao'
import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent} from '../controllers/eventor'

export const MeansState = {
  means: {},
  meansLoaded: false
}

MeansState.means.__proto__ = {
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
    MeansState.means[""+meansDto[i].id] = meansDto[i]
  }
  ResolveMeans();
}

export var ResolveMeans = function(){
  for(var i in MeansState.means){
    if(MeansState.means.hasOwnProperty(i)){
      resolveMean(MeansState.means[i])
    }
  }
}

const resolveMean = function(mean){
  mean.children = []
  mean.targets = []
  mean.__proto__ = protomean
  for(var j in MeansState.means){
    if(MeansState.means[j].parentid == mean.id){
      mean.children.push(MeansState.means[j])
    }
  }
  for(var tid in mean.targetsIds){
      var target = TargetsState.targets[mean.targetsIds[tid]]
      if(target!=null){
        mean.targets.push(target)
      }
  }
}

// export var AllMeans = function(callback){
//   if(!MeansState.meansLoaded){
//       $.ajax({url: "/mean/all/lazy"}).then(function(data) {
//                 var receivedData = typeof data == 'string'? JSON.parse(data): data
//                 importMeansDto(receivedData)
//                 if(callback != null)
//                   callback()
//               });
//     MeansState.meansLoaded = true
//   }
//   return MeansState.means
// }

export var CreateMean = function(id, title, realmid, targets, children){
  var childrenToAdd = children!=null? children: []
  var newMean = {
    'id': id,
    'title': title,
    'realmid': realmid,
    'targets': targets,
    'children': childrenToAdd,
  }
  newMean.__proto__ = protomean
  return newMean;
}

registerEvent('means-dao', 'means-request', function(){
    $.ajax({url: "/mean/all/lazy"}).then(function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importMeansDto(receivedData)
              MeansState.meansLoaded = true
              fireEvent('means-dao', 'means-received', [])
            });
})

registerEvent('means-dao', 'means-received', function(){

})

registerEvent('means-dao', 'create', function(mean, parent){
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
      MeansState.means[""+data.id] = data
      ResolveMeans()
      fireEvent('means-dao', 'mean-created', [mean])
    }
  });
})

registerEvent('means-dao', 'mean-created', function(mean){
  return mean
})

registerEvent('means-dao', 'delete', function(id, targetid){
  $.ajax({
    url: '/mean/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete MeansState.means[id]
      ResolveMeans()
      fireEvent('means-dao', 'mean-deleted', [id])
    }
  });
})

registerEvent('means-dao', 'mean-deleted', function(id){
  return id
})

// Remove mean that has only one target and that target has id = targetid
// Removing is only in UI because on server-side mean is removed automatically when target is removed
registerEvent('means-dao', 'delete-depended-means', function(targetid){
  for(var i in MeansState.means){
    if(MeansState.means.hasOwnProperty(i)){
      if(MeansState.means[i].targets.length == 1 && MeansState.means[i].targets[0].id == targetid){
        //delete means[i]
        deleteMeanUI(MeansState.means[i])
      } else {
        if(MeansState.means[i].targets.length>1){
          for(var j in MeansState.means[i].targets){
            if(MeansState.means[i].targets[j].id == targetid){
              delete MeansState.means[i].targets[j]
            }
          }
        }
      }
    }
  }
  ResolveMeans()
})

registerEvent('means-dao', 'modify', function(mean){
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
      MeansState.means[""+data.id] = data
      ResolveMeans()
      fireEvent('means-dao', 'mean-modified', [mean])
    }
  });
})

registerEvent('means-dao', 'mean-modified', function(mean){
  return mean
})

//delete Mean only form UI
var deleteMeanUI = function(mean){
  delete MeansState.means[mean.id]
  var parent = MeansState.means[mean.parentid]
  if(parent != null){
    parent.children = parent.children.filter(function(e){
      e.id!=mean.id
    })
  }
}

export var MeanById = function(id){
  return MeansState.means[id];
}
