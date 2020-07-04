import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button, Table} from 'react-bootstrap'

import {CommonModal} from './../common-modal'
import {TestingsList} from './testings-list'
import {formatDate} from '../../utils/date-utils'

import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

export class TaskProgressModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false, isSpacedRep:false, repPlan:null, isReadOnly:true}}
    this.state = defaultState()

    registerEvent('task-progress-modal', 'open', (stateSetter, task, isReadOnly)=>this.setState({isOpen:true, task:task, isReadOnly:isReadOnly}))
    registerEvent('task-progress-modal', 'close', (stateSetter)=>this.setState(defaultState()))

    registerReaction('task-progress-modal', 'tasks-dao', ['task-finished', 'repetition-finished'], ()=>{fireEvent('task-progress-modal', 'close')})
    registerReaction('task-progress-modal', 'tasks-dao', ['got-repetitions'], ()=>{this.setState({})})
    registerReaction('task-progress-modal', 'rep-plans-dao', ['plans-received'], ()=>{this.setState({})})
  }

  render(){
    var content = null
    if(this.state.task!=null && this.state.task.id>0){
      if(this.state.isReadOnly){
        content = finishRepetitionContent(this)
      } else {
        content = isFinishingTask(this)? finishTaskContent(this):finishRepetitionContent(this)
      }
    }
    return <CommonModal
                isOpen = {this.state.isOpen}
                okHandler = {isValid(this) && !this.state.isReadOnly?()=>okHandler(this):null}
                cancelHandler = {()=>fireEvent('task-progress-modal', 'close')}
                title={getTitle(this)}>
                {content}
          </CommonModal>
  }
}

const getTitle = function(comp){
  if(comp.state.isReadOnly){
    return 'Progress'
  }
  return isFinishingTask(comp)? "Finish task": "Finish repetition"
}

const isFinishingTask = function(reactcomp){
  return reactcomp.state.task.repetition==null
}

const okHandler = function(reactcomp){
  if(isFinishingTask(reactcomp)){
    fireEvent('tasks-dao', 'finish-task', [reactcomp.state.task, reactcomp.state.repPlan, reactcomp.state.task.testings])
  } else {
    fireEvent('tasks-dao', 'finish-repetition', [reactcomp.state.task, reactcomp.state.task.repetition])
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
  const repPlans = chkSt('rep-plans-dao', 'rep-plans')
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
            <div>{tableOfRepetitions(reactcomp)}</div>
          </div>
}

const tableOfRepetitions = function(reactcomp){
  const task = reactcomp.state.task
  if(task.repetitions != null){
    const result = []
    var count = 1
    task.repetitions.forEach(rep => {
      const style = task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
      result.push( <tr id={rep.id} style={style}>
                      <td>{count++}</td>
                      <td>{formatDate(rep.planDate)}</td>
                      <td>{rep.factDate!=null?formatDate(rep.factDate):''}</td>
                    </tr>)
    })
    return <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>#</td>
                <td>Plan date</td>
                <td>Fact date</td>
              </tr>
              {result}
            </tbody>
            </Table>
  } else {
    fireEvent('tasks-dao', 'get-repetitions', [task])
    return 'Loading...'
  }
}
