import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl, Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common/common-modal'
import {CommonCrudeTemplate} from './../../common/common-crud-template'
import {StatefulTextField} from '../../common/stateful-text-field'
import {TextArea} from '../../common/text-area'
import {DataConstants} from '../../../data/data-constants'

import {TopicsList} from './topics-list'
import {TestingsList} from './testings-list'

export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, mode: {isStatic: false, isEdit: false, progress:null}, layer: null, task: null, highlightId: null, showTestings: null}
    this.state = defaultState

    registerEvent('task-modal', 'open',
                (stateSetter, layer, task, highlightId)=>this.setState(getState(layer, task, highlightId)))

    registerEvent('task-modal', 'open-view-only',
                (stateSetter, layer, task, highlightId)=>this.setState({isOpen:true, mode: {isStatic: true, isEdit: false}, layer: layer, task: task, highlightId: highlightId}))

    registerEvent('task-modal', 'open-testings-hidden',(stateSetter, layer, task, highlightId)=>{
              const state = getState(layer, task, highlightId)
              state.showTestings = false
              this.setState(state)
            })

    registerEvent('task-modal', 'close', ()=>this.setState(defaultState))

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

const getState = function(layer, task, highlightId){
  var state = null
  if(task.id == null || task.id == DataConstants.newId){
    state = {isOpen:true, mode: {isStatic: true, isEdit: true}, layer: layer, task: task}
  } else {
    state = {isOpen:true, mode: {isStatic: false, isEdit: false}, layer: layer, task: task}
  }
  state.highlightId = highlightId
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
  if(component.state.showTestings==false){
    component.state.testingObjectives = ''
    return <div>
              <TextArea obj={component.state} valName={'testingObjectives'} valNameUI={'objectives'}/>
              <Button onClick={()=>component.setState({showTestings:true})}>Show testings</Button>
          </div>
  }
  return <TestingsList task={component.state.task} isEdit={component.state.mode.isEdit} testingsGuesses={component.state.testingObjectives} />
}

const progressButton = function(component){
  return <Button disabled={isProgressButtonDisabled(component)} onClick={()=>fireEvent('task-progress-modal', 'open', [component.state.task, component.state.highlightId])}>Progress</Button>
}

const isProgressButtonDisabled = function(component){
  return component.state.task.id == null || component.state.task.id == DataConstants.newId
}
