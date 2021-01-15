import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {CreateRealm} from './../../data/creators'
import {DataConstants} from '../../data/data-constants'

const newObjId = "new"
const hoursDefault = 1

const createState = function(isOpen, isStatic, isEdit, externalTask){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    externalTask: externalTask
  }
}

export class ExternalTaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = createState(false, true, false);

    registerEvent('external-task-modal', 'open', (stateSetter, externalTask) => this.setState(createState(true, true, true, externalTask)))
    registerEvent('external-task-modal', 'close', (stateSetter, externalTask) => this.setState(createState(false, true, false, null)))

    registerReaction('external-task-modal', DataConstants.externalTasksRep, ['created', 'updated'], ()=>this.setState(createState(false, true, false, null)))

  }

  render(){
    var content = null

    if(this.state.isOpen){
      content = <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>console.log('TODO deleting externalTask')}>
                <form>
                  <FormGroup controlId="formBasicText">
                    <ControlLabel>Description</ControlLabel>

                    {this.state.mode.isEdit? <FormControl
                      type="text"
                      value={this.state.externalTask.description}
                      placeholder="Enter title"
                      onChange={(e) => handleNameVal(e, this)}/>
                    : <FormControl.Static>{this.state.externalTask.description}</FormControl.Static>}

                  </FormGroup>
                </form>
              </CommonCrudeTemplate>
    }

    return <CommonModal isOpen = {this.state.isOpen} okHandler = {()=>okHandler(this.state.externalTask)} cancelHandler={()=>fireEvent('external-task-modal', 'close')} title={"External task"} >
          {content}
      </CommonModal>
  }
}

const okHandler = function(externalTask){

  if(externalTask.hours==null){
    externalTask.hours = hoursDefault
  }

  if(externalTask.id==null){
    fireEvent(DataConstants.externalTasksRep, 'create', [externalTask])
  } else {
    fireEvent(DataConstants.externalTasksRep, 'update', [externalTask])
  }
}

const handleNameVal = function(e, comp){
  comp.state.externalTask.description = e.target.value;
  comp.setState({})
}
