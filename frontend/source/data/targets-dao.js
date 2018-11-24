import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, getStateVal} from '../controllers/eventor'

registerEvent('targets-dao', 'create', function(stateSetter, target, parent){
  target.parentid = parent!=null? parent.id: null
  $.ajax({
    url: '/target/create',
    type: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(target),
    success: function(data) {
      getStateVal('targets-dao', 'targets')[""+data.id] = data
      resolveTargets()
      fireEvent('targets-dao', 'target-created', [target])
    }
  });
})

registerEvent('targets-dao', 'target-created', (stateSetter, target)=>target)

registerEvent('targets-dao', 'delete', function(stateSetter, id){
  $.ajax({
    url: '/target/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete getStateVal('targets-dao', 'targets')[id]
      resolveTargets()
      fireEvent('targets-dao', 'target-deleted', [id])
    }
  });
})

registerEvent('targets-dao', 'target-deleted', (stateSetter, id)=>id)

registerEvent('targets-dao', 'modify', function(stateSetter, target){
  $.ajax({
    url: '/target/update',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(target),
    success: function(data) {
      getStateVal('targets-dao', 'targets')[""+data.id] = data
      resolveTargets()
      fireEvent('targets-dao', 'target-modified', [target])
    }
  });
})

registerEvent('targets-dao', 'target-modified', (stateSetter, target)=>target)

registerEvent('targets-dao', 'targets-request', function(stateSetter){
    $.ajax({url: "/target/all/lazy"}).then(function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importTargetsDto(stateSetter, receivedData)
              fireEvent('targets-dao', 'targets-received', [])
            });
})

registerEvent('targets-dao', 'targets-received', ()=>{})

const targetsProto = {
  map: function(callback, filter){
    var result = []
    for (var i in this){
      if(this.hasOwnProperty(i)){
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

const targetsuper = {
  toString: function(){
    return this.title;
  }
}

const importTargetsDto = function(stateSetter, targetsDto){
  if(getStateVal('targets-dao', 'targets')==null){
    const targets = []
    targets.__proto__ = targetsProto
    stateSetter('targets', targets)
  }
  for(var i in targetsDto){
    getStateVal('targets-dao', 'targets')[""+targetsDto[i].id] = targetsDto[i]
  }
  resolveTargets();
}

const resolveTargets = function(){
  const targets = getStateVal('targets-dao', 'targets')
  for(var i in targets){
    if(targets.hasOwnProperty(i)){
      resolveTarget(targets[i])
    }
  }
}

const resolveTarget = function(target){
  target.children = []
  target.__proto__ = targetsuper
  const targets = getStateVal('targets-dao', 'targets')
  for(var j in targets){
    if(targets[j].parentid == target.id){
      target.children.push(targets[j])
    }
  }
}

export var GetTargetById = function(id){
  return getStateVal('targets-dao', 'targets').id
}
