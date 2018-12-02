import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'
import {insertObj, deleteObj, swapObjs} from './drag-utils'

registerEvent('subjects-dao', 'add-subject', (stateSetter, layer, subject)=>{
  if(layer.subjects==null){
    layer.subjects = []
  }
  var subjectToCreate = null
  if(subject==null){
    subjectToCreate = {position: getMaxSubjectPosition(layer.subjects)+1}
  } else {
    subjectToCreate = subject
    subjectToCreate.position = getMaxSubjectPosition(layer.subjects)+1
  }
  layer.subjects[subjectToCreate.position] = subjectToCreate
})

registerEvent('subjects-dao', 'add-subject-to-drag', (stateSetter, layer, subject)=>stateSetter('draggable-subject', {layer: layer, subject: subject}))
registerEvent('subjects-dao', 'release-draggable-subject', (stateSetter)=>stateSetter('draggable-subject', null))
registerEvent('subjects-dao', 'move-subject', (stateSetter, targetLayer, targetSubject)=>{
  const sourceLayer = viewStateVal('subjects-dao', 'draggable-subject').layer
  const sourceSubject = viewStateVal('subjects-dao', 'draggable-subject').subject
  if(targetSubject!=null){
    if(sourceSubject!=targetSubject){
      if(sourceLayer!=targetLayer){
        deleteObj(sourceLayer, sourceSubject, 'subjects', 'position')
        insertObj(targetLayer, sourceSubject, targetSubject.position, 'subjects', 'position')
        stateSetter('draggable-subject', {layer: targetLayer, subject: sourceSubject})
      } else {
        swapObjs(targetLayer, targetSubject.position, sourceSubject.position, 'subjects', 'position')
      }
    }
  } else {
    if(sourceLayer!=targetLayer){
      deleteObj(sourceLayer, sourceSubject, 'subjects', 'position')
      const nextPos = getMaxSubjectPosition(targetLayer.subjects)+1
      sourceSubject.position = nextPos
      if(targetLayer.subjects==null){
        targetLayer.subjects = []
      }
      targetLayer.subjects[nextPos] = sourceSubject
      stateSetter('draggable-subject', {layer: targetLayer, subject: sourceSubject})
    }
  }
})


const getMaxSubjectPosition = function(subjects){
    var result = 0
    if(subjects!=null){
      for(var subjecid in subjects){
        if(subjects[subjecid].position>result){
          result = subjects[subjecid].position
        }
      }
    }
    return result
}
