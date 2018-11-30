import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

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
