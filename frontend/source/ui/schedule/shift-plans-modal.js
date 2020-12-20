import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'


export class ShiftPlansModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, dayFrom: null, dayTo:null}
    this.state = defaultState

    registerEvent('shift-plans-modal', 'open', (stateSetter, dayFrom, dayTo)=>this.setState({isOpen:true, dayFrom:dayFrom, dayTo:dayTo}))
    registerEvent('shift-plans-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('shift-plans-modal', DataConstants.dayRep, ['got-one'], ()=>this.setState({}))
  }

  render(){
    return <CommonModal
                    isOpen = {this.state.isOpen}
                    cancelHandler = {()=>fireEvent('shift-plans-modal', 'close')}
                    title={getTitle(this)}>
                    {getContent(this)}
              </CommonModal>
  }
}

const getTitle = function(comp){
  if(comp.state.dayFrom == null || comp.state.dayTo == null){
    return ''
  }

  return 'Moving plans from day ' + comp.state.dayFrom.date +' to '+comp.state.dayTo.date
}

const getContent = function(comp){

  if(comp.state.dayFrom == null){
    return null
  }

  if(chkSt(DataConstants.dayRep, DataConstants.objMap) == null || chkSt(DataConstants.dayRep, DataConstants.objMap)[comp.state.dayFrom.id]==null
      || !comp.state.dayFrom.isFull){

    fireEvent(DataConstants.dayRep, 'get-one', [comp.state.dayFrom])
    return 'Loading...'
  }

  return <div>
            <div>
              Tasks
            </div>
            <div>
              {mappersTableUI(comp.state.dayFrom.taskMappers)}
            </div>
            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            <div>
              Repetitions
            </div>
            <div>
              {repetitionsTableUI(comp.state.dayFrom.repetitions)}
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
