import $ from 'jquery'
import {Protomean} from './creators'
import {registerEvent, registerReaction, fireEvent, viewStateVal, registerReactionCombo} from '../controllers/eventor'


registerEvent('means-dao', 'means-request', function(stateSetter){
    $.ajax({url: "/mean/all/lazy"}).then(function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importMeansDto(stateSetter, receivedData)
              fireEvent('means-dao', 'means-received', [])
            });
})

//registerReaction('means-dao', 'targets-dao', 'targets-received', ()=>fireEvent('means-dao', 'means-request'))

registerReactionCombo('means-dao', {'realms-dao':'realms-received', 'targets-dao': 'targets-received'}, ()=>fireEvent('means-dao', 'means-request'))

registerEvent('means-dao', 'means-received', ()=>{})

registerEvent('means-dao', 'create', function(stateSetter, mean, parent){
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
      viewStateVal('means-dao', 'means')[data.id] = data
      resolveMeans(viewStateVal('means-dao', 'means'))
      fireEvent('layers-dao', 'save-candidates', [data])
      fireEvent('means-dao', 'mean-created', [data])
    }
  });
})

registerEvent('means-dao', 'mean-created', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'delete', function(stateSetter, id, targetid){
  $.ajax({
    url: '/mean/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete viewStateVal('means-dao', 'means')[id]
      resolveMeans(viewStateVal('means-dao', 'means'))
      fireEvent('means-dao', 'mean-deleted', [id])
    }
  });
})

registerEvent('means-dao', 'mean-deleted', (stateSetter, id)=>id)

// Remove mean that has only one target and that target has id = targetid
// Removing is only in UI because on server-side mean is removed automatically when target is removed
registerEvent('means-dao', 'delete-depended-means', function(stateSetter, targetid){
  const means = viewStateVal('means-dao', 'means')
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
  resolveMeans(means)
})

registerEvent('means-dao', 'modify', function(stateSetter, mean){
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
      viewStateVal('means-dao', 'means')[data.id] = data
      resolveMeans(viewStateVal('means-dao', 'means'))
      fireEvent('layers-dao', 'save-candidates', [data])
      fireEvent('means-dao', 'mean-modified', [mean])
    }
  });
})

registerEvent('means-dao', 'mean-modified', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'add-draggable', (stateSetter, mean)=>stateSetter('draggableMean', mean))

registerEvent('means-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableMean', null))

registerEvent('means-dao', 'assign-quarter-to-draggable', function(stateSetter, quarter, position){
  const draggableMean = viewStateVal('means-dao', 'draggableMean')
  draggableMean.quarterid = quarter.id
  draggableMean.position = position
  fireEvent('means-dao', 'modify', [draggableMean])
})
registerEvent('means-dao', 'unassign-quarter', function(stateSetter, mean){
  mean.quarterid = null
  mean.position = null
  fireEvent('means-dao', 'modify', [mean])
})

const meansProto = {
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

const importMeansDto = function(stateSetter, meansDto){
  if(viewStateVal('means-dao', 'means')==null){
    const means = []
    means.__proto__ = meansProto
    stateSetter('means', means)
  }
  for(var i in meansDto){
    viewStateVal('means-dao', 'means')[meansDto[i].id] = meansDto[i]
  }
  resolveMeans(viewStateVal('means-dao', 'means'));
}

const resolveMeans = function(means){
  for(var i in means){
    if(means.hasOwnProperty(i)){
      resolveMean(means[i])
    }
  }
}

const resolveMean = function(mean){
  mean.children = []
  mean.targets = []
  mean.__proto__ = Protomean
  const means = viewStateVal('means-dao', 'means')
  for(var j in means){
    if(means[j].parentid == mean.id){
      mean.children.push(means[j])
    }
  }
  for(var tid in mean.targetsIds){
      var target = viewStateVal('targets-dao', 'targets')[mean.targetsIds[tid]]
      if(target!=null){
        mean.targets.push(target)
      }
  }
}

//delete Mean only form UI
var deleteMeanUI = function(mean){
  delete viewStateVal('means-dao', 'means')[mean.id]
  var parent = viewStateVal('means-dao', 'means')[mean.parentid]
  if(parent != null){
    parent.children = parent.children.filter(function(e){
      e.id!=mean.id
    })
  }
}

export var MeanById = function(id){
  return viewStateVal('means-dao', 'means')[id]
}
