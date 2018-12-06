import {sendGet, sendPut, sendPost, sendDelete} from './postoffice'
import {Protomean} from './creators'
import {registerEvent, registerReaction, fireEvent, viewStateVal, registerReactionCombo} from '../controllers/eventor'
import {getMaxVal} from '../utils/import-utils'
import {insertObj, deleteObj, swapObjs, addObj} from '../utils/drag-utils'
import {findHead, findLast, iterateLL} from '../utils/linked-list'


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
  // mean.position = parent!=null? getMaxVal(parent.children, 'position')+1:
  //         getMaxVal(viewStateVal('means-dao', 'means').map((mean)=>mean, (mean)=>mean.parentid==null), 'position')+1
  if(parent!=null){
    const head = findHead(parent.children, 'next')
    mean.nextid = head!=null? head.id: null
  }
  sendPut('/mean/create', JSON.stringify(mean), function(data) {
    viewStateVal('means-dao', 'means')[data.id] = data
    resolveMeans(viewStateVal('means-dao', 'means'))
    fireEvent('means-dao', 'mean-created', [data])
  })
})

registerEvent('means-dao', 'mean-created', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'delete', function(stateSetter, id, targetid){
  sendDelete('/mean/delete/'+id, function() {
    delete viewStateVal('means-dao', 'means')[id]
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
    //fireEvent('layers-dao', 'save-candidates', [data])
    fireEvent('means-dao', 'mean-modified', [mean])
  })
})

registerEvent('means-dao', 'mean-modified', (stateSetter, mean)=>mean)

registerEvent('means-dao', 'add-draggable', (stateSetter, mean)=>stateSetter('draggableMean', mean))
registerEvent('means-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableMean', null))

registerEvent('means-dao', 'replace-mean', (stateSetter, newParent, targetMean)=>{
  const meanToDrag = viewStateVal('means-dao', 'draggableMean')
  if(meanToDrag!=null && meanToDrag!=targetMean && !isMeanDescendsFrom(targetMean, meanToDrag)){
    const oldParent = meanToDrag.parentid!=null? viewStateVal('means-dao', 'means')[meanToDrag.parentid]:null
    if(oldParent!=null && oldParent==newParent){
      //swapObjs(oldParent, meanToDrag.position, targetMean.position, 'children', 'position')
    } else {
      if(newParent!=null){
        // insertObj(newParent, meanToDrag, targetMean.position, 'children', 'position')
        // meanToDrag.parentid = newParent!=null? newParent.id: null
      } else{
        // const root = generateRootArray(viewStateVal('means-dao', 'means'), 'parentid', 'position')
        // insertObj({children: root}, meanToDrag, targetMean.position, 'children', 'position')
        // meanToDrag.parentid = null
      }
    }
    //resolveMeans(viewStateVal('means-dao', 'means'))
  }
})

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
    stateSetter('means', means)
  }
  if(viewStateVal('means-dao', 'means-by-realm')==null){
    stateSetter('means-by-realm', means)
  }
  const means = viewStateVal('means-dao', 'means')
  for(var i in meansDto){
    const currentMeanDto = meansDto[i]
    if(means[currentMeanDto.realmid]==null){
      means[currentMeanDto.realmid] = []
    }
    means[currentMeanDto.realmid][meansDto[i].id] = meansDto[i]
  }
  for(var realmid in means){
    resolveMeans(means[realmid])
  }
  //resolveMeans(viewStateVal('means-dao', 'means'));
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

const isMeanDescendsFrom =function(child, searchParent){
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
