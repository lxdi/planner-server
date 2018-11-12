import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'

import {AddTarget, CreateTarget, CreateTargetWithIncrementedId} from './../../data/targets-dao'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'

const dumbTarget = CreateTarget(0, '')

const createState = function(){
  return {
    isOpen: false,
    isTargetModalOpen:false,
    mode: {isStatic: true, isEdit: false},
    parent: null,
    currentTarget: dumbTarget,

  }
}

const defaultState = function(){
  return {
    isOpen: false,
    isTargetModalOpen:false,
    parent: null,
    currentTarget: dumbTarget,
    mode: {isStatic: true, isEdit: false}
  }
}

export class TargetModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('target-modal', 'open', function(currentTarget, parent){
      this.setState({
        isOpen:true,
        mode: {
          isStatic:currentTarget.id==0,
          isEdit:currentTarget.id==0
        },
        currentTarget: currentTarget,
        parent: parent})
    }.bind(this))

    registerEvent('target-modal', 'close', function(){
      this.setState(defaultState())
    }.bind(this))

  }

  okHandler(){
    if(this.state.currentTarget.title != ''){
      if(this.state.currentTarget.id==0){
        fireEvent('targets-dao', 'create', [this.state.currentTarget, this.state.parent])
      } else {
        fireEvent('targets-dao', 'modify', [this.state.currentTarget])
      }
    } else {
      this.props.isTitleValid = false
      this.setState({})
    }
  }

  handleNameVal(e){
    this.state.currentTarget.title = e.target.value;
    this.setState({})
  }

  render(){
    return <CommonModal isOpen = {this.state.isOpen} okHandler = {this.okHandler} cancelHandler={()=>fireEvent('target-modal', 'close', [])} title={targetModalHeaderTitle} >
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent('targets-dao', 'delete', [this.state.currentTarget.id])}>
          <form>
            <FormGroup controlId="formBasicText">
              <ControlLabel>Title</ControlLabel>
              {this.state.mode.isEdit? <FormControl
                type="text"
                value={this.state.currentTarget.title}
                placeholder="Enter title"
                onChange={this.handleNameVal}/>
              : <FormControl.Static>{this.state.currentTarget.title}</FormControl.Static>}
              {this.state.isTitleValid!=null && !this.state.isTitleValid? <Alert bsStyle="danger"><strong>Title</strong> must not be empty</Alert>: null}
            </FormGroup>
          </form>
        </CommonCrudeTemplate>
      </CommonModal>
  }
}
