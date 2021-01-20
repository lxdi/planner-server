import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common-modal'
import {formatDate} from '../../../utils/date-utils'
import {DataConstants} from '../../../data/data-constants'


export class TaskFinishModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, task: null}
    this.state = defaultState

    registerEvent('task-finish-modal', 'open', (stateSetter, task, plans)=>this.setState({isOpen:true, task:task, plans: plans}))
    registerEvent('task-finish-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('task-finish-modal', DataConstants.progressRep, 'got-by-task', ()=>this.setState(defaultState))

  }

  render(){
    return  <CommonModal
                    isOpen = {this.state.isOpen}
                    okHandler = {isValid(this)? ()=>okHandler(this):null}
                    cancelHandler = {()=>fireEvent('task-finish-modal', 'close')}
                    title='Finish task'>
                    {getContent(this)}
              </CommonModal>
  }
}

const okHandler = function(comp){
  if(comp.state.chosenPlan == null){
    fireEvent(DataConstants.progressRep, 'finish-task', [comp.state.task])
  } else {
    fireEvent(DataConstants.progressRep, 'finish-task-sp', [comp.state.task, comp.state.chosenPlan])
  }
}

const isValid = function(component){
  if(component.state.isSpacedRep){
    if(component.state.chosenPlan==null){
      return false
    }
  }
  return true
}

const getContent = function(reactcomp){
  if(reactcomp.state.task == null){
    return null
  }
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
  const repPlans = reactcomp.state.plans
  const divStyle = reactcomp.state.chosenPlan==null?{display:'inline-block', border:'1px solid red'}:null
  return <div>
              <div style={divStyle}>
                <ButtonToolbar>
                  <DropdownButton bsSize="small"
                          title={reactcomp.state.chosenPlan!=null?reactcomp.state.chosenPlan.title:'Select Repetition plan'}
                          id="dropdown-size-small" onSelect={(chosenPlan)=>reactcomp.setState({chosenPlan: chosenPlan})}>
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
