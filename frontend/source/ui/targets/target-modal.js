import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CreateTarget} from './../../data/creators'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'

const newObjId = "new"
const targetRep = 'target-rep'
const targetModalId = 'target-modal'

const createState = function(isOpen, isStatic, isEdit, currentTarget){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    currentTarget: currentTarget,
    fieldsValidation:{}
  }
}

const defaultState = function(){
  return createState(false, true, false, null)
}

export class TargetModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent(targetModalId, 'open', function(state, currentTarget){
      this.setState(createState(true, currentTarget.id==newObjId, currentTarget.id==newObjId, currentTarget))
    }.bind(this))

    registerEvent(targetModalId, 'close', function(){
      this.setState(defaultState())
    }.bind(this))

    registerReaction(targetModalId, targetRep, ['updated', 'created', 'deleted'], ()=>{
      fireEvent(targetModalId, 'close')
    })

  }

  okHandler(){
    if(this.state.currentTarget.title != ''){
      if(this.state.currentTarget.id==newObjId){
        fireEvent(targetRep, 'create', [this.state.currentTarget])
      } else {
        fireEvent(targetRep, 'update', [this.state.currentTarget])
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
    if(this.state.currentTarget == null){
      return null
    }

    return <CommonModal isOpen = {this.state.isOpen} okHandler = {this.okHandler} cancelHandler={()=>fireEvent(targetModalId, 'close', [])} title={targetModalHeaderTitle} >
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent(targetRep, 'delete', [this.state.currentTarget])}>
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
