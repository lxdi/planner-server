import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common-modal'
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
  }

  render(){
    return <CommonModal
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
          </div>
}

//--------------------------Mappers--------------------------------

const mappersTableUI = function(mappers){
  const result = []
  mappers.forEach(mapper => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={mapper.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("task-modal", 'open', [null, mapper.taskSelf, true, false, mapper.id])}>{mapper.taskSelf.fullPath}</a>
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
                      <a href='#' onClick={()=>fireEvent("task-modal", 'open', [null, rep.taskSelf, true, false, rep.id])}>{rep.taskSelf.fullPath}</a>
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
