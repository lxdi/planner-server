import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem} from 'react-bootstrap'

import {CommonModal} from './../common-modal'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class FinishingTaskModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false, isSpacedRep:false, repPlan:null}}
    this.state = defaultState()

    registerEvent('finishing-task-modal', 'open', (stateSetter, task)=>this.setState({isOpen:true, task:task}))
    registerEvent('finishing-task-modal', 'close', (stateSetter)=>this.setState(defaultState()))

    registerReaction('finishing-task-modal', 'tasks-dao', ['task-finished'], ()=>{fireEvent('finishing-task-modal', 'close')})
    registerReaction('finishing-task-modal', 'rep-plans-dao', ['plans-received'], ()=>{this.setState({})})
  }

  render(){
    return <CommonModal
                isOpen = {this.state.isOpen}
                okHandler = {()=>fireEvent('tasks-dao', 'finish-task', [this.state.task, this.state.repPlan])}
                cancelHandler = {()=>fireEvent('finishing-task-modal', 'close')}
                title="Finish task">
                {content(this)}
          </CommonModal>
  }
}

const content = function(reactcomp){
  return <div>
          {reactcomp.state.isSpacedRep==false?spacedRepRadioButton(reactcomp):null}
          <div>
            {reactcomp.state.isSpacedRep?repPlanChooserUI(reactcomp):null}
          </div>
        </div>
}

const spacedRepRadioButton = function(reactcomp){
  return <div>
              <input type="radio" autocomplete="off" checked={reactcomp.state.isSpacedRep} style={{marginRight:'5px'}} onClick={()=>reactcomp.setState({isSpacedRep: !reactcomp.state.isSpacedRep})} />
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
