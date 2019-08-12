import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {Protomean} from './creators'
import {registerEvent, registerReaction, fireEvent, chkSt, registerReactionCombo} from 'absevents'
import {getMaxVal} from '../utils/import-utils'
import {addToLastLL} from '../utils/linked-list'
import {replaceDraggableUtil, addAsChildDraggableUtil, mergeArrays} from '../utils/draggable-tree-utils'
import {normalizeInnerArrays} from '../utils/import-utils'


registerEvent('means-dao', 'means-request', function(stateSetter){
    sendGet("/mean/all/lazy", function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importMeansDto(stateSetter, receivedData)
              fireEvent('means-dao', 'means-received', [])
            })
})

registerEvent('means-dao', 'means-received', ()=>{})

registerReactionCombo('means-dao', {'realms-dao':'realms-received', 'targets-dao': 'targets-received'}, ()=>fireEvent('means-dao', 'means-request'))

registerEvent('means-dao', 'get-full', (stateSetter, mean)=>{
  sendGet('/mean/full/'+mean.id, (data)=>{
    Object.assign(chkSt('means-dao', 'means')[mean.realmid][mean.id], data)
    mean.isFull = true
    normalizeInnerArrays(mean, [{arrName:'layers', posName:'priority'}, {arrName:'subjects', posName:'position'}, {arrName:'tasks', posName:'position'}])
    fireEvent('means-dao', 'got-full', [mean])
  })
})

registerEvent('means-dao', 'got-full', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'create', function(stateSetter, mean, parent){
  mean.parentid = parent!=null? parent.id: null
  sendPut('/mean/create', JSON.stringify(mean), function(data) {
    if(data.previd!=null){
      chkSt('means-dao', 'means')[data.realmid][data.previd].nextid = data.id
    }
    importOneMeanDto(data)
    resolveMean(data)
    if(parent!=null){
      parent.targetsIds = []
    }
    fireEvent('means-dao', 'mean-created', [data])
  })
})

registerEvent('means-dao', 'mean-created', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'delete', function(stateSetter, id, targetid){
  sendDelete('/mean/delete/'+id, function() {
    deleteMeanUI(chkSt('means-dao', 'means')[chkSt('realms-dao', 'currentRealm').id][id])
    fireEvent('means-dao', 'mean-deleted', [id])
  })
})

registerEvent('means-dao', 'mean-deleted', (stateSetter, id)=>id)

// Remove mean that has only one target and that target has id = targetid
// Removing is only in UI because on server-side mean is removed automatically when target is removed
registerEvent('means-dao', 'delete-depended-means', function(stateSetter, target){
  const means = chkSt('means-dao', 'means')[target.realmid]
  //TODO now there is no mean.targets array, only ids
  // for(var i in means){
  //   if(means.hasOwnProperty(i)){
  //     if(means[i].targets.length == 1 && means[i].targets[0].id == targetid){
  //       deleteMeanUI(means[i])
  //     } else {
  //       if(means[i].targets.length>1){
  //         for(var j in means[i].targets){
  //           if(means[i].targets[j].id == targetid){
  //             delete means[i].targets[j]
  //           }
  //         }
  //       }
  //     }
  //   }
  // }
})

registerEvent('means-dao', 'modify', function(stateSetter, mean){
  sendPost('/mean/update', JSON.stringify(mean), function(data) {
    importOneMeanDto(data)
    resolveMean(chkSt('means-dao', 'means')[data.realmid][data.id])
    fireEvent('means-dao', 'mean-modified', [mean])
    fireEvent('hquarters-dao', 'hquarters-request')
  })
})

registerEvent('means-dao', 'mean-modified', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'modify-list', function(stateSetter, means){
  sendPost('/mean/reposition/list', JSON.stringify(means), function(data) {
    for(var i in data){
      importOneMeanDto(data[i])
      resolveMean(chkSt('means-dao', 'means')[data[i].realmid][data[i].id])
    }
    fireEvent('means-dao', 'means-list-modified', [data])
  })
})

registerEvent('means-dao', 'means-list-modified', (stateSetter, means)=>means)

registerEvent('means-dao', 'hide-children', (stateSetter, mean)=>{
  sendPost('/mean/'+mean.id+'/hideChildren/'+mean.hideChildren, null, (data)=>{
    Object.assign(mean, data)
    fireEvent('means-dao', 'hide-children-changed', [mean])
  })
})

registerEvent('means-dao', 'hide-children-changed', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'add-draggable', (stateSetter, mean)=>{
  stateSetter('draggableMean', mean)
  fireEvent('means-frame', 'update')
  fireEvent('schedule-frame', 'update')
})

registerEvent('means-dao', 'remove-draggable', (stateSetter)=>{
  stateSetter('draggableMean', null)
  fireEvent('means-frame', 'update')
  fireEvent('schedule-frame', 'update')
})

const importMeansDto = function(stateSetter, meansDto){
  if(chkSt('means-dao', 'means')==null){
    stateSetter('means', [])
  }
  for(var i in meansDto){
    importOneMeanDto(meansDto[i])
    resolveMean(chkSt('means-dao', 'means')[meansDto[i].realmid][meansDto[i].id])
  }
}

const importOneMeanDto = function(meanDto){
  const means = chkSt('means-dao', 'means')
  if(means[meanDto.realmid]==null){
    means[meanDto.realmid] = []
  }
  means[meanDto.realmid][meanDto.id] = meanDto
}

const resolveMean = function(mean){
  mean.__proto__ = Protomean
}

//delete Mean only form UI
const deleteMeanUI = function(mean){
  const means = chkSt('means-dao', 'means')[chkSt('realms-dao', 'currentRealm').id]
  for(var id in means){
    if(means[id].nextid == mean.id){
      means[id].nextid = mean.nextid
      break
    }
  }
  delete means[mean.id]
}

export var MeanById = function(id){
  return chkSt('means-dao', 'means')[id]
}
