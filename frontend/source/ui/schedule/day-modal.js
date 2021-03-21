import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common/common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'


export class DayModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, day: null}
    this.state = defaultState

    registerEvent('day-modal', 'open', (stateSetter, day)=>this.setState({isOpen:true, day:day}))
    registerEvent('day-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('day-modal', DataConstants.dayRep, ['got-one'], ()=>this.setState({}))

    registerReaction('day-modal', DataConstants.externalTasksRep, ['created', 'updated'], ()=>{
      fireEvent(DataConstants.dayRep, 'clean-all')
      this.setState({})
    })

    registerReaction('day-modal', DataConstants.progressRep, ['got-by-task'], ()=>{
      fireEvent(DataConstants.dayRep, 'clean-all')
      this.setState({})
    })
  }

  render(){
    return <CommonModal styleClass={'day-modal-style'}
                    isOpen = {this.state.isOpen}
                    cancelHandler = {()=>fireEvent('day-modal', 'close')}
                    title='Day'>
                    {getContent(this)}
              </CommonModal>
  }
}

const getContent = function(comp){

  if(comp.state.day == null){
    return null
  }

  if(chkSt(DataConstants.dayRep, DataConstants.objMap) == null || chkSt(DataConstants.dayRep, DataConstants.objMap)[comp.state.day.id]==null
      || !comp.state.day.isFull){

    fireEvent(DataConstants.dayRep, 'get-one', [comp.state.day])
    return 'Loading...'
  }

  return <div>
            <div>
              Tasks
            </div>
            <div>
              {mappersTableUI(comp.state.day.taskMappers)}
            </div>

            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            <div>
              Repetitions
            </div>
            <div>
              {repetitionsTableUI(comp.state.day.repetitions)}
            </div>

            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            <div>
              External Tasks
            </div>
            <div>
              <Button bsStyle="default" bsSize='xsmall' onClick={() => fireEvent('external-task-modal', 'open', [{dayid: comp.state.day.id}])}>Add +</Button>
            </div>
            <div>
              {externalTasksTableUI(comp.state.day.externalTasks)}
            </div>
          </div>
}

//--------------------------Mappers--------------------------------

const mappersTableUI = function(mappers){
  const result = []
  mappers.forEach(mapper => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={mapper.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("mean-modal", 'open-with-task', [mapper.mean, mapper.taskSelf.id])}>{mapper.taskSelf.fullPath}</a>
                    </td>
                    <td>{mapper.planDay!=null? formatDate(mapper.planDay.date):''}</td>
                    <td>{mapper.finishDay!=null? formatDate(mapper.finishDay.date):''}</td>
                    <td>
                      {mapper.finishDay==null? <Button bsStyle="success" bsSize='xsmall' onClick={() => fireEvent(DataConstants.progressRep, 'finish-rep', [mapper, mapper.taskSelf])}>Complete</Button>: null}
                    </td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>Task</td>
              <td>Plan date</td>
              <td>Finish date</td>
              <td></td>
            </tr>
            {result}
          </tbody>
          </Table>
}


//--------------------------Repetitions--------------------------------

const repetitionsTableUI = function(repetitions){
  const result = []
  repetitions.forEach(rep => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={rep.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent('mean-modal', 'open-with-task', [rep.mean, rep.taskSelf.id, rep.id])}>{rep.taskSelf.fullPath}</a>
                    </td>
                    <td>{rep.planDay!=null? formatDate(rep.planDay.date):''}</td>
                    <td>{rep.factDay!=null? formatDate(rep.factDay.date):''}</td>
                    <td>
                      {rep.factDay==null? <Button bsStyle="success" bsSize='xsmall' onClick={() => fireEvent(DataConstants.progressRep, 'finish-rep', [rep, rep.taskSelf])}>Complete</Button>: null}
                    </td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>Task</td>
              <td>Plan date</td>
              <td>Fact date</td>
              <td></td>
            </tr>
            {result}
          </tbody>
          </Table>
}

//--------------------------------------ExternalTasks--------------------------------

const externalTasksTableUI = function(extTasks){
  const result = []
  extTasks.forEach(extTask => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={extTask.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("external-task-modal", 'open', [extTask])}>{extTask.description}</a>
                    </td>
                    <td>{extTask.hours}</td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>Description</td>
              <td>Hours</td>
            </tr>
            {result}
          </tbody>
          </Table>
}
