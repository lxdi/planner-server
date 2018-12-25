//import $ from 'jquery'
import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('targets-dao', 'create', function(stateSetter, target, parent){
  target.parentid = parent!=null? parent.id: null
  sendPut('/target/create', JSON.stringify(target), function(data) {
    if(data.previd!=null){
      viewStateVal('targets-dao', 'targets')[data.realmid][data.previd].nextid = data.id
    }
    importOneTargetDto(data)
    resolveTarget(data)
    fireEvent('targets-dao', 'target-created', [data])
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
    importOneTargetDto(data)
    resolveTarget(viewStateVal('targets-dao', 'targets')[data.realmid][data.id])
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

registerEvent('targets-dao', 'modify-list', function(stateSetter, targets){
  sendPost('/target/update/list', JSON.stringify(targets), function(data) {
    for(var i in data){
      importOneTargetDto(data[i])
      resolveTarget(viewStateVal('targets-dao', 'targets')[data[i].realmid][data[i].id])
    }
    fireEvent('targets-dao', 'targets-list-modified', [data])
  })
})

registerEvent('targets-dao', 'targets-list-modified', (stateSetter, targets)=>targets)

registerEvent('targets-dao', 'add-draggable', (stateSetter, target)=>{stateSetter('draggableTarget', target)})

registerEvent('targets-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableTarget', null))

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
    stateSetter('targets', [])
  }
  for(var i in targetsDto){
    importOneTargetDto(targetsDto[i])
    resolveTarget(viewStateVal('targets-dao', 'targets')[targetsDto[i].realmid][targetsDto[i].id])
  }
  //resolveTargets();
}

const importOneTargetDto = function(targetDto){
  const targets = viewStateVal('targets-dao', 'targets')
  if(targets[targetDto.realmid]==null){
    targets[targetDto.realmid] = []
  }
  targets[targetDto.realmid][targetDto.id] = targetDto
}

const resolveTarget = function(target){
  target.__proto__ = targetsuper
}

export var GetTargetById = function(id){
  return viewStateVal('targets-dao', 'targets').id
}
