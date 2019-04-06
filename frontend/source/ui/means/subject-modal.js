import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevent'

const createState = function(isOpen, isStatic, isEdit, layer, subject){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    layer: layer,
    subject: subject
  }
}

export class SubjectModal extends React.Component {
  constructor(props){
    super(props)
    this.state = createState(false, false, false, null, null)
    registerEvent('subject-modal', 'open', (stateSetter, layer, subject)=>this.setState(getState(layer, subject)))
    registerEvent('subject-modal', 'close', ()=>this.setState(createState(false, false, false, null, null)))

    registerReaction('subject-modal', 'subjects-dao', 'subject-deleted', (stateSetter)=>fireEvent('subject-modal', 'close'))
  }

  render(){
    return <CommonModal title="Subject"
                        isOpen={this.state.isOpen}
                        okHandler={isSubjectValid(this.state.subject)?()=>okHandler(this.state.layer, this.state.subject):null}
                        cancelHandler={()=>fireEvent('subject-modal', 'close')}>
              {this.state.subject!=null? modalContent(this): null}
            </CommonModal>
  }
}

const getState = function(layer, subject){
  if(subject.id == null || subject.id == 0){
    return createState(true, true, true, layer, subject)
  } else {
    return createState(true, false, false, layer, subject)
  }
}

const isSubjectValid = function(subject){
  if(subject!=null && subject.title!=null && subject.title!=''){
    return true
  } else {
    return false
  }
}

const okHandler = function(layer, subject){
  if(subject.id==null){
    fireEvent('subjects-dao', 'add-subject', [layer, subject])
    subject.id = 0
  }
  fireEvent('subject-modal', 'close')
}

const modalContent = function(component){
  if(component.state.subject.title == null){
    component.state.subject.title = ''
  }
  return   <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {()=>component.setState({})}
                  deleteHandler={()=>fireEvent('subjects-dao', 'delete-subject', [component.state.layer, component.state.subject])}>
                <form>
                    <FormGroup controlId="formBasicText">
                    <div style={{display:'inline-block', paddingRight:'5px'}}><ControlLabel>Title:</ControlLabel></div>
                    <div style={{display:'inline-block'}}>{component.state.mode.isEdit?<FormControl
                        type="text"
                        value={component.state.subject.title}
                        placeholder="Enter title"
                        onChange={(e)=>{component.state.subject.title = e.target.value; component.setState({})}}/>
                    :<FormControl.Static>{component.state.subject.title}</FormControl.Static>}</div>
                    </FormGroup>
                  </form>
          </CommonCrudeTemplate>
}
