import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

const createState = function(isOpen, isStatic, isEdit, subject, task){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    subject: subject,
    task: task
  }
}

export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = createState(false, false, false, null, null)
    registerEvent('task-modal', 'open', (stateSetter, subject, task)=>this.setState(getState(subject, task)))
    registerEvent('task-modal', 'close', ()=>this.setState(createState(false, false, false, null, null)))

    registerReaction('task-modal', 'tasks-dao', 'task-deleted', (stateSetter)=>fireEvent('task-modal', 'close'))
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen} okHandler={isTaskValid(this.state.task)?()=>okHandler(this.state.subject, this.state.task):null}>
              {this.state.task!=null? modalContent(this): null}
            </CommonModal>
  }
}

const getState = function(subject, task){
  if(task.id == null || task.id == 0){
    return createState(true, true, true, subject, task)
  } else {
    return createState(true, false, false, subject, task)
  }
}

const isTaskValid = function(task){
  return task!=null && task.title!=null && task.title!=''
}

const okHandler = function(subject, task){
  if(task.id==null){
    fireEvent('tasks-dao', 'add-task', [subject, task])
    task.id = 0
  }
  fireEvent('task-modal', 'close')
}

const modalContent = function(component){
  if(component.state.task.title == null){
    component.state.task.title = ''
  }
  return      <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {()=>component.setState({})}
                  deleteHandler={()=>fireEvent('tasks-dao', 'delete-task', [component.state.subject, component.state.task])}>
                <form>
                    <FormGroup controlId="formBasicText">
                    <ControlLabel>Title</ControlLabel>
                    {component.state.mode.isEdit? <FormControl
                        type="text"
                        value={component.state.task.title}
                        placeholder="Enter title"
                        onChange={(e)=>{component.state.task.title = e.target.value; component.setState({})}}/>
                      :<FormControl.Static>{component.state.task.title}</FormControl.Static>}
                    </FormGroup>
                  </form>
                </CommonCrudeTemplate>
}
