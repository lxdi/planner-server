import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl, Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common-modal'
import {CommonCrudeTemplate} from './../../common-crud-template'
import {StatefulTextField} from '../../common/stateful-text-field'
import {TextArea} from '../../common/text-area'
import {DataConstants} from '../../../data/data-constants'

import {TopicsList} from './topics-list'
import {TestingsList} from './testings-list'

const createState = function(isOpen, isStatic, isEdit, layer, task, progress){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit, progress:progress},
    layer: layer,
    task: task
  }
}

export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = createState(false, false, false, null, null)

    registerEvent('task-modal', 'open',
                (stateSetter, layer, task, isViewOnly, withprogress)=>this.setState(getState(layer, task, isViewOnly, withprogress)))

    registerEvent('task-modal', 'close', ()=>this.setState(createState(false, false, false, null, null, null)))

    registerReaction('task-modal', DataConstants.taskRep, ['task-deleted', 'repetition-finished'], (stateSetter)=>fireEvent('task-modal', 'close'))
    registerReaction('task-modal', DataConstants.taskRep, 'task-finished', (stateSetter)=>this.setState({}))
  }

  render(){
    const isViewOnly = this.state.mode.isStatic && !this.state.mode.isEdit
    return <CommonModal title="Task"
                        isOpen={this.state.isOpen}
                        cancelHandler={()=>fireEvent('task-modal', 'close')}
                        okHandler={!isViewOnly && isTaskValid(this.state.task)?()=>okHandler(this.state.layer, this.state.task):null}
                        styleClass="task-modal-style">
              {this.state.task!=null? modalContent(this): null}
            </CommonModal>
  }
}

const getState = function(layer, task, isViewOnly, withprogress){
  var state = null
  if(task.id == null || task.id == DataConstants.newId){
    state = createState(true, true, true, layer, task)
  } else {
    state = createState(true, false, false, layer, task)
  }
  if(isViewOnly!=null && isViewOnly==true){
    state.mode.isStatic = true
    state.mode.isEdit = false
  }
  if(withprogress==true){
    state.mode.progress = true
  }
  state.showTestings = false
  return state
}

const isTaskValid = function(task){
  return task!=null && task.title!=null && task.title!=''
}

const okHandler = function(layer, task){
  if(task.id == DataConstants.newId){
    if(layer.tasks == null){
      layer.tasks = []
    }
    layer.tasks.push(task)
  }

  fireEvent('task-modal', 'close')
}

const modalContent = function(component){
  if(component.state.task.title == null){
    component.state.task.title = ''
  }
  return      <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {()=>component.setState({})}
                  deleteHandler={()=>removeTask(component)}>
                <form>
                    {progressButton(component)}
                    <FormGroup controlId="formBasicText">
                      <div style={{display:'inline-block', paddingRight:'5px'}}><ControlLabel>Title:</ControlLabel></div>

                      <div style={{display:'inline-block'}}>
                        <StatefulTextField obj={component.state.task} valName={'title'} isEdit={component.state.mode.isEdit} onInput={()=>component.setState({})}/>
                      </div>

                      <TopicsList task={component.state.task} isEdit={component.state.mode.isEdit} />
                      {getTestingsUI(component)}
                    </FormGroup>
                  </form>
                </CommonCrudeTemplate>
}

const removeTask = function(component){
  const index = component.state.layer.tasks.indexOf(component.state.task)
  component.state.layer.tasks.splice(index, 1)
  fireEvent('task-modal', 'close')
}

const getTestingsUI = function(component){
  if(component.state.showTestings==true || !(component.state.mode.isEdit==false && component.state.mode.isStatic==true)){
    return <TestingsList task={component.state.task} isEdit={component.state.mode.isEdit} testingsGuesses={component.state.testingObjectives} />
  }
  component.state.testingObjectives = ''
  return <div>
            <TextArea obj={component.state} valName={'testingObjectives'} valNameUI={'objectives'}/>
            <Button onClick={()=>component.setState({showTestings:true})}>Show testings</Button>
        </div>
}

const progressButton = function(component){
  return <Button disabled={isProgressButtonDisabled(component)} onClick={()=>fireEvent('task-progress-modal', 'open', [component.state.task])}>Progress</Button>
}

const isProgressButtonDisabled = function(component){
  return component.state.task.id == null || component.state.task.id == DataConstants.newId
}
