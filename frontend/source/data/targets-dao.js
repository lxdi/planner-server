//import $ from 'jquery'
import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('targets-dao', 'create', function(stateSetter, target, parent){
  target.parentid = parent!=null? parent.id: null
  sendPut('/target/create', JSON.stringify(target), function(data) {
    viewStateVal('targets-dao', 'targets')[""+data.id] = data
    resolveTargets()
    fireEvent('targets-dao', 'target-created', [target])
  })
})

registerEvent('targets-dao', 'target-created', (stateSetter, target)=>target)

registerEvent('targets-dao', 'delete', function(stateSetter, id){
  sendDelete('/target/delete/'+id, function() {
    delete viewStateVal('targets-dao', 'targets')[id]
    resolveTargets()
    fireEvent('targets-dao', 'target-deleted', [id])
  })
})

registerEvent('targets-dao', 'target-deleted', (stateSetter, id)=>id)

registerEvent('targets-dao', 'modify', function(stateSetter, target){
  sendPost('/target/update', JSON.stringify(target), function(data) {
    viewStateVal('targets-dao', 'targets')[""+data.id] = data
    resolveTargets()
    fireEvent('targets-dao', 'target-modified', [target])
  })
})

registerEvent('targets-dao', 'target-modified', (stateSetter, target)=>target)

registerEvent('targets-dao', 'targets-request', function(stateSetter){
  sendGet("/target/all/lazy", function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importTargetsDto(stateSetter, receivedData)
            fireEvent('targets-dao', 'targets-received', [])
          })
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
  if(viewStateVal('targets-dao', 'targets')==null){
    const targets = []
    targets.__proto__ = targetsProto
    stateSetter('targets', targets)
  }
  for(var i in targetsDto){
    viewStateVal('targets-dao', 'targets')[""+targetsDto[i].id] = targetsDto[i]
  }
  resolveTargets();
}

const resolveTargets = function(){
  const targets = viewStateVal('targets-dao', 'targets')
  for(var i in targets){
    if(targets.hasOwnProperty(i)){
      resolveTarget(targets[i])
    }
  }
}

const resolveTarget = function(target){
  target.children = []
  target.__proto__ = targetsuper
  const targets = viewStateVal('targets-dao', 'targets')
  for(var j in targets){
    if(targets[j].parentid == target.id){
      target.children.push(targets[j])
    }
  }
}

export var GetTargetById = function(id){
  return viewStateVal('targets-dao', 'targets').id
}
