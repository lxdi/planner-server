import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {Protomean} from './creators'
import {registerEvent, registerReaction, fireEvent, viewStateVal, registerReactionCombo} from '../controllers/eventor'
import {getMaxVal} from '../utils/import-utils'
import {addToLastLL} from '../utils/linked-list'
import {replaceDraggableUtil, addAsChildDraggableUtil, mergeArrays} from '../utils/draggable-tree-utils'


registerEvent('means-dao', 'means-request', function(stateSetter){
    sendGet("/mean/all/lazy", function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importMeansDto(stateSetter, receivedData)
              fireEvent('means-dao', 'means-received', [])
            })
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
  mean.realmid = viewStateVal('realms-dao', 'currentRealm').id
  sendPut('/mean/create', JSON.stringify(mean), function(data) {
    //-- need this to update a nextid locally of means which are peering to the new mean
    //addToLastLL(viewStateVal('means-dao', 'means')[data.parentid].children, data)
    // if(data.parentid==null){
    //   if(viewStateVal('means-dao', 'means-by-realm')[data.realmid]==null){
    //     viewStateVal('means-dao', 'means-by-realm')[data.realmid]=[]
    //   }
    //   addToLastLL(viewStateVal('means-dao', 'means-by-realm')[data.realmid], data)
    // }
    //--
    if(data.previd!=null){
      viewStateVal('means-dao', 'means')[data.realmid][data.previd].nextid = data.id
    }
    importOneMeanDto(data)
    //resolveMeans(viewStateVal('means-dao', 'means'))
    resolveMean(data)
    fireEvent('means-dao', 'mean-created', [data])
  })
})

registerEvent('means-dao', 'mean-created', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'delete', function(stateSetter, id, targetid){
  sendDelete('/mean/delete/'+id, function() {
    deleteMeanUI(viewStateVal('means-dao', 'means')[id])
    //resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'mean-deleted', [id])
  })
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
  //resolveMeans(means)
})

registerEvent('means-dao', 'modify', function(stateSetter, mean){
  mean.targetsIds = []
  for(var i in mean.targets){
    mean.targetsIds.push(mean.targets[i].id)
  }
  sendPost('/mean/update', JSON.stringify(mean), function(data) {
    viewStateVal('means-dao', 'means')[data.id] = data
    //resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'mean-modified', [mean])
  })
})

registerEvent('means-dao', 'mean-modified', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'modify-list', function(stateSetter, means){
  sendPost('/mean/update/list', JSON.stringify(means), function(data) {
    for(var i in data){
      //viewStateVal('means-dao', 'means')[data[i].id] = data[i]
      importOneMeanDto(data[i])
      resolveMean(viewStateVal('means-dao', 'means')[data[i].realmid][data[i].id])
    }
    //resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'means-list-modified', [data])
  })
})

registerEvent('means-dao', 'means-list-modified', (stateSetter, means)=>means)

registerEvent('means-dao', 'add-draggable', (stateSetter, mean)=>{stateSetter('draggableMean', mean)})

registerEvent('means-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableMean', null))

// const meansProto = {
//   map: function(callback, filter){
//     var result = []
//     for (var i in this){
//       if(i != 'map'){
//         if(filter!=null){
//           if(filter(this[i])){
//             result.push(callback(this[i]))
//           }
//         } else {
//           result.push(callback(this[i]))
//         }
//       }
//     }
//     return result
//   }
// }

const importMeansDto = function(stateSetter, meansDto){
  if(viewStateVal('means-dao', 'means')==null){
    stateSetter('means', [])
  }
  for(var i in meansDto){
    importOneMeanDto(meansDto[i])
    resolveMean(viewStateVal('means-dao', 'means')[meansDto[i].realmid][meansDto[i].id])
  }
  //resolveMeans(viewStateVal('means-dao', 'means'));
}

const importOneMeanDto = function(meanDto){
  const means = viewStateVal('means-dao', 'means')
  if(means[meanDto.realmid]==null){
    means[meanDto.realmid] = []
  }
  means[meanDto.realmid][meanDto.id] = meanDto
}

const resolveMean = function(mean){
  mean.targets = []
  mean.__proto__ = Protomean
  for(var tid in mean.targetsIds){
      var target = viewStateVal('targets-dao', 'targets')[mean.targetsIds[tid]]
      if(target!=null){
        mean.targets.push(target)
      }
  }
}

//delete Mean only form UI
const deleteMeanUI = function(mean){
  const means = viewStateVal('means-dao', 'means')
  for(var id in means){
    if(means[id].nextid == mean.id){
      means[id].nextid = mean.nextid
      break
    }
  }
  delete means[mean.id]
  if(mean.parentid==null){
    delete viewStateVal('means-dao', 'root-means-by-realm')[mean.realmid][mean.id]
  }
}

export var MeanById = function(id){
  return viewStateVal('means-dao', 'means')[id]
}
