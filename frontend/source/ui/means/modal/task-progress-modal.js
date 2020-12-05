import React from 'react';
import ReactDOM from 'react-dom';
import {ButtonToolbar, DropdownButton, MenuItem, Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common-modal'
import {formatDate} from '../../../utils/date-utils'
import {DataConstants} from '../../../data/data-constants'

//props: task
export class TaskProgressModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, task: null}
    this.state = defaultState

    registerEvent('task-progress-modal', 'open', (stateSetter, task)=>this.setState({isOpen:true, task:task}))
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
    return task.title
  }
  return null
}

const getContent = function(component){
  const task = component.state.task

  if(task == null){
    return null
  }

  var progressByTask = chkSt(DataConstants.progressRep, DataConstants.objMap)
  if(progressByTask == null){
    fireEvent(DataConstants.progressRep, 'get-by-task', [task])
    return 'Lading...'
  }

  const progress = progressByTask[task.id]

  return <div>
            {getMappers(progress, task)}
            <div style = {{borderBottom: '1px solid grey', padding: '3px'}}  />
            {getRepetitions(progress)}
          </div>
}


const getMappers = function(progress, task){
  var content = []
  progress.taskMappers.forEach(taskMapper => content.push(mapperUI(taskMapper)))
  return <div>
            <div>Finishes</div>
            {content}
            <Button bsStyle="default" onClick={() => fireEvent('task-finish-modal', 'open', [task, progress.plans])}>Finish</Button>
          </div>
}

const mapperUI = function(taskMapper){
  return <div>
            {taskMapper.planDay!=null?taskMapper.planDay.date:'-'}/{taskMapper.finishDay!=null?taskMapper.finishDay.date:'-'}
          </div>
}

const getRepetitions = function(progress){
  var content = []
  progress.repetitions.forEach(repetition => content.push(repetitionUI(repetition)))
  return <div>
            <div>Repetitions</div>
            {content}
          </div>
}

const repetitionUI = function(repetition){
  return <div>
            {repetition.planDay!=null?repetition.planDay.date:'-'}/{repetition.factDay!=null?repetition.factDay.date:'-'}
        </div>
}
