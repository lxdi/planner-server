//import $ from 'jquery'
import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {deleteNode} from '../utils/import-utils'

registerEvent('targets-dao', 'create', function(stateSetter, target, parent){
  target.parentid = parent!=null? parent.id: null
  sendPut('/target/create', JSON.stringify(target), function(data) {
    if(data.previd!=null){
      chkSt('targets-dao', 'targets')[data.realmid][data.previd].nextid = data.id
    }
    importOneTargetDto(data)
    resolveTarget(data)
    fireEvent('targets-dao', 'target-created', [data])
  })
})

registerEvent('targets-dao', 'target-created', (stateSetter, target)=>target)

registerEvent('targets-dao', 'delete', function(stateSetter, target){
  sendDelete('/target/delete/'+target.id, function() {
    deleteNode(chkSt('targets-dao', 'targets')[target.realmid], target)
    //delete chkSt('targets-dao', 'targets')[target.realmid][id]
    fireEvent('targets-dao', 'target-deleted', [target])
  })
})

registerEvent('targets-dao', 'target-deleted', (stateSetter, target)=>target)

registerEvent('targets-dao', 'modify', function(stateSetter, target){
  sendPost('/target/update', JSON.stringify(target), function(data) {
    importOneTargetDto(data)
    resolveTarget(chkSt('targets-dao', 'targets')[data.realmid][data.id])
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
      resolveTarget(chkSt('targets-dao', 'targets')[data[i].realmid][data[i].id])
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
  if(chkSt('targets-dao', 'targets')==null){
    stateSetter('targets', [])
  }
  for(var i in targetsDto){
    importOneTargetDto(targetsDto[i])
    resolveTarget(chkSt('targets-dao', 'targets')[targetsDto[i].realmid][targetsDto[i].id])
  }
  //resolveTargets();
}

const importOneTargetDto = function(targetDto){
  const targets = chkSt('targets-dao', 'targets')
  if(targets[targetDto.realmid]==null){
    targets[targetDto.realmid] = []
  }
  targets[targetDto.realmid][targetDto.id] = targetDto
}

const resolveTarget = function(target){
  target.__proto__ = targetsuper
}

export var GetTargetById = function(id){
  return chkSt('targets-dao', 'targets').id
}
