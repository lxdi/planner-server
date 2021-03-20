import React from 'react';
import ReactDOM from 'react-dom';
import {Modal, Button} from 'react-bootstrap'

import {registerEvent, fireEvent} from 'absevents'

import {CommonModal} from './common-modal'

export class ConfirmModal extends React.Component{
  constructor(props){
    super(props)

    const defaultState = {isOpen:false, message:null, action: null}
    this.state = defaultState

    registerEvent('confirm-modal', 'open', (stateSetter, message, action)=>this.setState({isOpen:true, message:message, action: action}))
    registerEvent('confirm-modal', 'close', (stateSetter)=>this.setState(defaultState))
  }

  render(){
    return (
      <CommonModal
        isOpen = {this.state.isOpen}
        okHandler = {()=>{this.state.action.call(); fireEvent('confirm-modal', 'close')}}
        cancelHandler = {()=>fireEvent('confirm-modal', 'close')}
        title={'Confirm'}
      >
        {this.state.message}
      </CommonModal>
    )
  }
}
