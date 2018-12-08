import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {Protomean} from './creators'
import {registerEvent, registerReaction, fireEvent, viewStateVal, registerReactionCombo} from '../controllers/eventor'
import {getMaxVal} from '../utils/import-utils'
import {insertObj, deleteObj, swapObjs, addObj} from '../utils/drag-utils'
import {swapLL, insertLL, removeFromLL, addToLastLL} from '../utils/linked-list'


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
  sendPut('/mean/create', JSON.stringify(mean), function(data) {
    //-- need this to update a nextid locally of means which are peering to the new mean
    if(data.parentid!=null){
      addToLastLL(viewStateVal('means-dao', 'means')[data.parentid].children, data)
    } else{
      if(viewStateVal('means-dao', 'root-means-by-realm')[data.realmid]==null){
        viewStateVal('means-dao', 'root-means-by-realm')[data.realmid]=[]
      }
      addToLastLL(viewStateVal('means-dao', 'root-means-by-realm')[data.realmid], data)
    }
    //--
    importOneMeanDto(data)
    resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'mean-created', [data])
  })
})

registerEvent('means-dao', 'mean-created', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'delete', function(stateSetter, id, targetid){
  sendDelete('/mean/delete/'+id, function() {
    deleteMeanUI(viewStateVal('means-dao', 'means')[id])
    resolveMeans(viewStateVal('means-dao', 'means'))
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
  resolveMeans(means)
})

registerEvent('means-dao', 'modify', function(stateSetter, mean){
  mean.targetsIds = []
  for(var i in mean.targets){
    mean.targetsIds.push(mean.targets[i].id)
  }
  sendPost('/mean/update', JSON.stringify(mean), function(data) {
    viewStateVal('means-dao', 'means')[data.id] = data
    resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'mean-modified', [mean])
  })
})

registerEvent('means-dao', 'mean-modified', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'modify-list', function(stateSetter, means){
  sendPost('/mean/update/list', JSON.stringify(means), function(data) {
    for(var i in data){
      //viewStateVal('means-dao', 'means')[data[i].id] = data[i]
      importOneMeanDto(data[i])
    }
    resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'means-list-modified', [data])
  })
})

registerEvent('means-dao', 'means-list-modified', (stateSetter, means)=>means)

registerEvent('means-dao', 'add-draggable', (stateSetter, mean)=>{stateSetter('draggableMean', mean)})

registerEvent('means-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableMean', null))

registerEvent('means-dao', 'draggable-save-altered', (stateSetter)=>{
  const alteredMeans = viewStateVal('means-dao', 'draggable-altered')
  if(alteredMeans!=null){
    fireEvent('means-dao', 'modify-list', [alteredMeans])
    stateSetter('draggable-altered', null)
  }
})

registerEvent('means-dao', 'replace-mean', (stateSetter, newParent, targetMean)=>{
  const meanToDrag = viewStateVal('means-dao', 'draggableMean')
  if(meanToDrag!=null && meanToDrag!=targetMean && !isMeanDescendsFrom(targetMean, meanToDrag)){
    var altered = viewStateVal('means-dao', 'draggable-altered')
    if(altered==null){
      altered = []
      stateSetter('draggable-altered', altered)
    }
    const oldParent = meanToDrag.parentid!=null? viewStateVal('means-dao', 'means')[meanToDrag.parentid]:null
    if(oldParent==null && newParent==null){
      //swap within root
       mergeArrays(altered, swapLL(viewStateVal('means-dao', 'root-means-by-realm')[meanToDrag.realmid], meanToDrag, targetMean))
    }
    if(oldParent==null && newParent!=null){
      //insert to new parent
      mergeArrays(altered, removeFromLL(viewStateVal('means-dao', 'root-means-by-realm')[meanToDrag.realmid], meanToDrag))
      mergeArrays(altered, insertLL(newParent.children, targetMean, meanToDrag))
      meanToDrag.parentid = newParent.id
    }
    if(oldParent!=null && newParent==null){
      //insert to root
      mergeArrays(altered, removeFromLL(oldParent.children, meanToDrag))
      mergeArrays(altered, insertLL(viewStateVal('means-dao', 'root-means-by-realm')[meanToDrag.realmid], targetMean, meanToDrag))
      meanToDrag.parentid = null
    }
    if(oldParent!=null && newParent!=null){
      if(oldParent==newParent){
        //swap within new parent (or within old parent)
        mergeArrays(altered, swapLL(oldParent.children, meanToDrag, targetMean))
      } else {
        //insert to new parent
        mergeArrays(altered, removeFromLL(oldParent.children, meanToDrag))
        mergeArrays(altered, insertLL(newParent.children, targetMean, meanToDrag))
        meanToDrag.parentid = newParent.id
      }
    }
    resolveMeans(viewStateVal('means-dao', 'means'))
  }
})

registerEvent('means-dao', 'draggable-add-as-child', (stateSetter, parent)=>{
  const meanToDrag = viewStateVal('means-dao', 'draggableMean')
  const oldParent = meanToDrag.parentid!=null? viewStateVal('means-dao', 'means')[meanToDrag.parentid]:null
  if(meanToDrag!=null && !isMeanDescendsFrom(parent, meanToDrag) && parent!=oldParent){
    var altered = viewStateVal('means-dao', 'draggable-altered')
    if(altered==null){
      altered = []
      stateSetter('draggable-altered', altered)
    }
    if(oldParent!=null){
      mergeArrays(altered, removeFromLL(oldParent.children, meanToDrag))
    }else {
      mergeArrays(altered, removeFromLL(viewStateVal('means-dao', 'root-means-by-realm')[meanToDrag.realmid], meanToDrag))
    }
    if(parent!=null){
      mergeArrays(altered, addToLastLL(parent.children, meanToDrag))
      meanToDrag.parentid = parent.id
    } else {
      mergeArrays(altered, addToLastLL(viewStateVal('means-dao', 'root-means-by-realm')[meanToDrag.realmid], meanToDrag))
      meanToDrag.parentid = null
    }
    resolveMeans(viewStateVal('means-dao', 'means'))
  }
})

const mergeArrays = function(mainArr, arr2){
  for(var i in arr2){
    if(mainArr.indexOf(arr2[i])<0){
      mainArr.push(arr2[i])
    }
  }
}

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
  if(viewStateVal('means-dao', 'root-means-by-realm')==null){
    stateSetter('root-means-by-realm', [])
  }
  for(var i in meansDto){
    importOneMeanDto(meansDto[i])
  }
  resolveMeans(viewStateVal('means-dao', 'means'));
}

const importOneMeanDto = function(meanDto){
  const means = viewStateVal('means-dao', 'means')
  const rootMeansByRealm = viewStateVal('means-dao', 'root-means-by-realm')
  if(rootMeansByRealm[meanDto.realmid]==null){
    rootMeansByRealm[meanDto.realmid] = []
  }
  if(meanDto.parentid==null){
    rootMeansByRealm[meanDto.realmid][meanDto.id] = meanDto
  }
  means[meanDto.id] = meanDto
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
      mean.children[means[j].id]=means[j]
      mean.length++
    }
  }
  for(var tid in mean.targetsIds){
      var target = viewStateVal('targets-dao', 'targets')[mean.targetsIds[tid]]
      if(target!=null){
        mean.targets.push(target)
      }
  }
}

const isMeanDescendsFrom =function(child, searchParent){
  if(child==null || searchParent==null){
    return false
  }
  if(child==searchParent){
    return true
  }
  const parent = child.parentid!=null? viewStateVal('means-dao', 'means')[child.parentid]: null
  if(parent!=null){
    if(parent==searchParent){
      return true
    } else {
      return isMeanDescendsFrom(parent, searchParent)
    }
  } else {
    return false
  }
}

// const generateRootArray = function(objs, fieldName, positionFieldName){
//   const result = []
//   for(var i in objs){
//     const obj = objs[i]
//     if(obj[fieldName]==null){
//       result[obj[positionFieldName]] = obj
//     }
//   }
//   return result
// }

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
