import React from 'react';
import ReactDOM from 'react-dom';

import {CommonModal} from './../common-modal'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class FinishingTaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {isOpen:false}

    registerEvent('finishing-task-modal', 'open', (stateSetter, task)=>this.setState({isOpen:true, task:task}))
    registerEvent('finishing-task-modal', 'close', (stateSetter)=>this.setState({isOpen:false, task:null}))

    registerReaction('finishing-task-modal', 'tasks-dao', ['task-finished'], ()=>{fireEvent('finishing-task-modal', 'close')})
  }

  render(){
    return <CommonModal
                isOpen = {this.state.isOpen}
                okHandler = {()=>fireEvent('tasks-dao', 'finish-task', [this.state.task])}
                cancelHandler = {()=>fireEvent('finishing-task-modal', 'close')}
                title="Finish task">
          </CommonModal>
  }
}
