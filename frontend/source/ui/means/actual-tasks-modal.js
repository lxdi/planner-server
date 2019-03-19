import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

export class ActualTasksModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false}}
    this.state = defaultState()
    registerEvent('actual-tasks-modal', 'open', ()=>this.setState({isOpen:true}))
    registerEvent('actual-tasks-modal', 'close', ()=>this.setState(defaultState()))
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen}
                cancelHandler={()=>fireEvent('actual-tasks-modal', 'close')}>
            <div>
              TEST
            </div>
          </CommonModal>
  }
}
