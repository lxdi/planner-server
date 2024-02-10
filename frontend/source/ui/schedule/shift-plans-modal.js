import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common/common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'

const getMovingDto = function(day){
  return {
    targetDayId: day!=null? day.id: null,
    taskMappersIds: [],
    repetitionIds: [],
    externalTasksIds: []
  }
}

export class ShiftPlansModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, dayFrom: null, dayTo:null, movingDto: getMovingDto()}
    this.state = defaultState

    registerEvent('shift-plans-modal', 'open', (stateSetter, dayFrom, dayTo)=>this.setState({isOpen:true, dayFrom:dayFrom, dayTo:dayTo, movingDto: getMovingDto(dayTo)}))
    registerEvent('shift-plans-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('shift-plans-modal', DataConstants.dayRep, ['got-one'], ()=>this.setState({}))
    registerReaction('shift-plans-modal', DataConstants.weekRep, ['moved-plans'], ()=>this.setState(defaultState))
  }

  render(){
    return <CommonModal
                    isOpen = {this.state.isOpen}
                    okHandler={()=>fireEvent(DataConstants.weekRep, 'move-plans', [this.state.movingDto])}
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
              {mappersTableUI(comp, comp.state.dayFrom.taskMappers, comp.state.movingDto)}
            </div>
            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            <div>
              Repetitions
            </div>
            <div>
              {repetitionsTableUI(comp, comp.state.dayFrom.repetitions, comp.state.movingDto)}
            </div>
            <div>
              External Tasks
            </div>
            <div>
              {exTasksTableUI(comp, comp.state.dayFrom.externalTasks, comp.state.movingDto)}
            </div>
          </div>
}

//--------------------------Mappers--------------------------------

const mappersTableUI = function(comp, mappers, movingDto){
  const result = []
  mappers.forEach(mapper => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={mapper.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("task-modal", 'open', [null, mapper.taskid, true, false, mapper.id])}>{mapper.fullPath}</a>
                    </td>
                    <td>{mapper.planDay!=null? formatDate(mapper.planDay.date):''}</td>
                    <td>{mapper.finishDay!=null? formatDate(mapper.finishDay.date):''}</td>
                    <td>
                      <input type="checkbox" checked={movingDto.taskMappersIds.includes(mapper.id)?'checked': null} onClick={()=>onClickCheckBox(comp, movingDto, mapper.id, 'task mapper')}/>
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

const repetitionsTableUI = function(comp, repetitions, movingDto){
  const result = []
  repetitions.forEach(rep => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={rep.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("task-modal", 'open', [null, rep.taskid, true, false, rep.id])}>{rep.fullPath}</a>
                    </td>
                    <td>{rep.planDay!=null? formatDate(rep.planDay.date):''}</td>
                    <td>{rep.factDay!=null? formatDate(rep.factDay.date):''}</td>
                    <td>
                      <input type="checkbox" checked={movingDto.repetitionIds.includes(rep.id)?'checked': null} onClick={()=>onClickCheckBox(comp, movingDto, rep.id, 'repetition')}/>
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

//--------------------------ExternalTasks--------------------------------

const exTasksTableUI = function(comp, tasks, movingDto){
  const result = []
  tasks.forEach(exTask => {
    const style = {} // task.repetition != null && task.repetition.id == rep.id? {fontWeight:'bold'}:{}
    result.push( <tr id={exTask.id} style={style}>
                    <td>
                      <a href='#' onClick={()=>fireEvent("external-task-modal", 'open', [exTask])}>{exTask.description}</a>
                    </td>
                    <td>{exTask.day!=null? formatDate(exTask.day.date):''}</td>
                    <td>{exTask.finished? 'Finished':''}</td>
                    <td>
                      <input type="checkbox" checked={movingDto.externalTasksIds.includes(exTask.id)?'checked': null} onClick={()=>onClickCheckBox(comp, movingDto, exTask.id, 'external task')}/>
                    </td>
                  </tr>)
  })

  return <Table striped bordered condensed hover >
          <tbody>
            <tr>
              <td>Task</td>
              <td>Date</td>
              <td>Status</td>
              <td></td>
            </tr>
            {result}
          </tbody>
          </Table>
}

const onClickCheckBox = function(comp, movingDto, id, type){
  if(type == 'task mapper'){
    updateId(movingDto.taskMappersIds, id)
  }

  if (type == 'repetition') {
    updateId(movingDto.repetitionIds, id)
  }

  if (type == 'external task') {
    updateId(movingDto.externalTasksIds, id)
  }

  comp.setState({})
}

const updateId = function(arr, id) {
  if(arr.includes(id)){
    removeByValue(arr, id)
  } else {
    arr.push(id)
  }
}

const removeByValue = function(array, item){
  var index = array.indexOf(item);
  if (index !== -1) {
    array.splice(index, 1);
  }
}
