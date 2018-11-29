import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('subjects-dao', 'add-subject', (stateSetter, layer)=>{
  if(layer.subjects==null){
    layer.subjects = []
  }
  const subject = {
    position: getMaxSubjectPosition(layer.subjects)+1
  }
  layer.subjects[subject.position] = subject
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
