import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button} from 'react-bootstrap'

import {CommonModal} from './../common-modal'
import {TestingsList} from './testings-list'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from 'absevent'

//props: testings
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
                okHandler = {isValid(this)?()=>okHandler(this):null}
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
    fireEvent('tasks-dao', 'finish-task', [reactcomp.state.task, reactcomp.state.repPlan, reactcomp.state.task.testings])
  } else {
    fireEvent('tasks-dao', 'finish-repetition', [reactcomp.state.task, reactcomp.state.repetition])
  }
}

const isValid = function(component){
  if(component.state.isSpacedRep){
    if(component.state.repPlan==null){
      return false
    }
  }
  return true
}

//------------------------------------------------------------------------

const finishTaskContent = function(reactcomp){
  if(reactcomp.state.isSpacedRep){
    if(reactcomp.state.task.testings==null){
      reactcomp.state.task.testings = []
    }
  }
  return <div>
          {spacedRepRadioButton(reactcomp)}
          <div>
            {reactcomp.state.isSpacedRep?repPlanChooserUI(reactcomp):null}
          </div>
          <div>
            {reactcomp.state.isSpacedRep?<TestingsList testings={reactcomp.state.task.testings} isEdit={true} />:null}
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
  const divStyle = reactcomp.state.repPlan==null?{display:'inline-block', border:'1px solid red'}:null
  return <div>
              <div style={divStyle}>
                <ButtonToolbar>
                  <DropdownButton bsSize="small"
                          title={reactcomp.state.repPlan!=null?reactcomp.state.repPlan.title:'Select Repetition plan'}
                          id="dropdown-size-small" onSelect={(repPlan)=>reactcomp.setState({repPlan: repPlan})}>
                    {availableRepPlans(repPlans)}
                  </DropdownButton>
                </ButtonToolbar>
              </div>
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
