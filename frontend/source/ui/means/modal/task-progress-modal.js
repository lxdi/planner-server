import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common-modal'
import {formatDate} from '../../../utils/date-utils'
import {DataConstants} from '../../../data/data-constants'

export class TaskProgressModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, task: null, highlightId: null}
    this.state = defaultState

    registerEvent('task-progress-modal', 'open', (stateSetter, task, highlightId)=>this.setState({isOpen:true, task:task, highlightId: highlightId}))
    registerEvent('task-progress-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('task-progress-modal', DataConstants.progressRep, 'got-by-task', ()=>this.setState({}))

  }

  render(){
    return  <CommonModal
                    isOpen = {this.state.isOpen}
                    cancelHandler = {()=>fireEvent('task-progress-modal', 'close')}
                    title={getTitle(this)}>
                    {getContent(this)}
              </CommonModal>
  }
}

const getTitle = function(component){
  const task = component.state.task
  if(task!=null){
    return 'Progress for ' + task.title
  }
  return null
}

const getContent = function(component){
  const task = component.state.task

  if(task == null){
    return null
  }

  var progressByTask = chkSt(DataConstants.progressRep, DataConstants.objMap)
  if(progressByTask == null || progressByTask[task.id] == null){
    fireEvent(DataConstants.progressRep, 'get-by-task', [task])
    return 'Loading...'
  }

  const progress = progressByTask[task.id]

  return <div>
            <div>
              <div>Finishes</div>
              {mappersTableUI(progress.taskMappers, component.state.highlightId)}
              <Button bsStyle="default" onClick={() => fireEvent('task-finish-modal', 'open', [task, progress.plans])}>Finish</Button>
            </div>
            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            <div>
              <div>Repetitions</div>
              {repetitionsTableUI(progress.repetitions, task, component.state.highlightId)}
            </div>
          </div>
}

//-------------------------Mappers--------------------------------

const mappersTableUI = function(mappers, highlightId){
  var count = 1
  const result = []
  mappers.forEach(mapper => {
    const style = mapper.id ==highlightId? {fontWeight: 'bold'}: {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={mapper.id} style={style}>
                    <td>{count++}</td>
                    <td>{mapper.planDay!=null? formatDate(mapper.planDay.date):''}</td>
                    <td>{mapper.finishDay!=null? formatDate(mapper.finishDay.date):''}</td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>#</td>
              <td>Plan date</td>
              <td>Finish date</td>
            </tr>
            {result}
          </tbody>
          </Table>
}

//-----------------------Repetitions------------------------------------

const repetitionsTableUI = function(repetitions, task, highlightId){
  var count = 1
  const result = []
  repetitions.forEach(rep => {
    var style = rep.id==highlightId? {fontWeight: 'bold'}: {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}

    if(rep.isMemo){
      Object.assign(style, {color: 'grey'})
    }

    result.push( <tr id={rep.id} style={style}>
                    <td>{count++}</td>
                    <td>{rep.planDay!=null? formatDate(rep.planDay.date):''}</td>
                    <td>{rep.factDay!=null? formatDate(rep.factDay.date):''}</td>
                    <td>
                      {rep.factDay==null && rep.id == highlightId? <Button bsStyle="success" bsSize='xsmall' onClick={() => fireEvent(DataConstants.progressRep, 'finish-rep', [rep, task])}>Complete</Button>: null}
                    </td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>#</td>
              <td>Plan date</td>
              <td>Fact date</td>
              <td></td>
            </tr>
            {result}
          </tbody>
          </Table>
}
