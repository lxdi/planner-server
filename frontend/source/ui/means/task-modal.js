import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import {CommonModal} from './../common-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {isOpen:null}
    registerEvent('task-modal', 'open', (stateSetter, subject, task)=>this.setState({isOpen: true, subject:subject, task: task}))
    registerEvent('task-modal', 'close', ()=>this.setState({isOpen: false, subject: null, task: null}))
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen}
            okHandler={()=>{fireEvent('tasks-dao', 'add-task', [this.state.subject, this.state.task]); fireEvent('task-modal', 'close')}}>
              {this.state.task!=null? modalContent(this): null}
            </CommonModal>
  }
}

const modalContent = function(component){
  if(component.state.task.title == null){
    component.state.task.title = ''
  }
  return   <form>
                    <FormGroup controlId="formBasicText">
                    <ControlLabel>Title</ControlLabel>
                    <FormControl
                        type="text"
                        value={component.state.task.title}
                        placeholder="Enter title"
                        onChange={(e)=>{component.state.task.title = e.target.value; component.setState({})}}/>
                    </FormGroup>
                  </form>
}
