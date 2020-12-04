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

  return '' + progress.taskMappers.length + '/' + progress.repetitions.length
}
