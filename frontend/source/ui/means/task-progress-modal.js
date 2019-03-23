import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button} from 'react-bootstrap'

import {CommonModal} from './../common-modal'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class TaskProgressModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false, isSpacedRep:false, repPlan:null, repetition:null}}
    this.state = defaultState()

    registerEvent('task-progress-modal', 'open', (stateSetter, task, repetition)=>this.setState({isOpen:true, task:task, repetition:repetition}))
    registerEvent('task-progress-modal', 'close', (stateSetter)=>this.setState(defaultState()))

    registerReaction('task-progress-modal', 'tasks-dao', ['task-finished', 'repetition-finished'], ()=>{fireEvent('task-progress-modal', 'close')})
    registerReaction('task-progress-modal', 'rep-plans-dao', ['plans-received'], ()=>{this.setState({})})
  }

  render(){
    return <CommonModal
                isOpen = {this.state.isOpen}
                okHandler = {()=>okHandler(this)}
                cancelHandler = {()=>fireEvent('task-progress-modal', 'close')}
                title={isFinishingTask(this)? "Finish task": "Finish repetition"}>
                {isFinishingTask(this)?finishTaskContent(this):finishRepetitionContent(this)}
          </CommonModal>
  }
}

const isFinishingTask = function(reactcomp){
  return reactcomp.state.repetition==null
}

const okHandler = function(reactcomp){
  if(isFinishingTask(reactcomp)){
    fireEvent('tasks-dao', 'finish-task', [reactcomp.state.task, reactcomp.state.repPlan])
  } else {
    fireEvent('tasks-dao', 'finish-repetition', [reactcomp.state.task, reactcomp.state.repetition])
  }
}

//------------------------------------------------------------------------

const finishTaskContent = function(reactcomp){
  return <div>
          {spacedRepRadioButton(reactcomp)}
          <div>
            {reactcomp.state.isSpacedRep?repPlanChooserUI(reactcomp):null}
          </div>
        </div>
}

const spacedRepRadioButton = function(reactcomp){
  return <div>
              <input type="radio" autocomplete="off" checked={reactcomp.state.isSpacedRep} style={{marginRight:'5px'}} onClick={()=>reactcomp.setState({isSpacedRep: true})} />
              Spaced repetition
            </div>
}

const repPlanChooserUI = function(reactcomp){
  const repPlans = viewStateVal('rep-plans-dao', 'rep-plans')
  if(repPlans == null){
    fireEvent('rep-plans-dao', 'plans-request')
    return 'Loading...'
  }
  return <div>
              <ButtonToolbar>
                <DropdownButton bsSize="small"
                        title={reactcomp.state.repPlan!=null?reactcomp.state.repPlan.title:'Select Repetition plan'}
                        id="dropdown-size-small" onSelect={(repPlan)=>reactcomp.setState({repPlan: repPlan})}>
                  {availableRepPlans(repPlans)}
                </DropdownButton>
              </ButtonToolbar>
          </div>
}

const availableRepPlans = function(repPlans){
  const result = []
  for(var i in repPlans){
    result.push(<MenuItem eventKey={repPlans[i]}>{repPlans[i].title}</MenuItem>)
  }
  return result
}

//------------------------------------------------------------------------

const finishRepetitionContent = function(reactcomp){
  return <div>
            <div style={{display:'inline-block'}}>{reactcomp.state.repetition.planDate}</div>
          </div>
}
