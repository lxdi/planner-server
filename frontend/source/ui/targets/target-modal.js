import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CreateTarget} from './../../data/creators'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'

const dumbTarget = CreateTarget(0, '')

const createState = function(isOpen, isStatic, isEdit, currentTarget, parent){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    currentTarget: currentTarget,
    parent: parent,
    fieldsValidation:{}
  }
}

const defaultState = function(){
  return createState(false, true, false, dumbTarget, null)
}

export class TargetModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('target-modal', 'open', function(state, currentTarget, parent){
      this.setState(createState(true, currentTarget.id==0, currentTarget.id==0, currentTarget, parent))
    }.bind(this))

    registerEvent('target-modal', 'close', function(){
      this.setState(defaultState())
    }.bind(this))

    registerReaction('target-modal', 'targets-dao', ['target-created', 'target-modified', 'target-deleted'], ()=>{
      fireEvent('target-modal', 'close')
    })

  }

  okHandler(){
    if(this.state.currentTarget.title != ''){
      if(this.state.currentTarget.id==0){
        fireEvent('targets-dao', 'create', [this.state.currentTarget, this.state.parent])
      } else {
        fireEvent('targets-dao', 'modify', [this.state.currentTarget])
      }
    } else {
      this.state.fieldsValidation['title'] = false
      this.setState({})
    }
  }

  handleNameVal(e, fieldName){
    if(this.state.fieldsValidation[fieldName] == false){
      this.state.fieldsValidation[fieldName] = true
    }
    this.state.currentTarget[fieldName] = e.target.value;
    this.setState({})
  }

  render(){
    return <CommonModal isOpen = {this.state.isOpen} okHandler = {this.okHandler} cancelHandler={()=>fireEvent('target-modal', 'close', [])} title={targetModalHeaderTitle} >
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent('targets-dao', 'delete', [this.state.currentTarget])}>
          <form>
            {getTextField(this, 'title', 'Title')}
            {getTextField(this, 'definitionOfDone', 'Definition of done')}
          </form>
        </CommonCrudeTemplate>
      </CommonModal>
  }
}

const getTextField = function(comp, fieldName, fieldTitle){
  var content = null
  if(comp.state.mode.isEdit){
    content = <FormControl
      type="text"
      value={comp.state.currentTarget[fieldName]}
      placeholder={fieldTitle}
      onChange={(e)=>comp.handleNameVal(e, fieldName)}/>
  } else {
    content = <FormControl.Static>{comp.state.currentTarget[fieldName]}</FormControl.Static>
  }
  return  <FormGroup controlId="formBasicText">
                <ControlLabel>{fieldTitle}</ControlLabel>
                {content}
                {comp.state.fieldsValidation[fieldName]!=null && !comp.state.fieldsValidation[fieldName]? <Alert bsStyle="danger"><strong>{fieldTitle}</strong> must not be empty</Alert>: null}
              </FormGroup>
}
