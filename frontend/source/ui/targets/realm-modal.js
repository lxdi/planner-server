import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CreateRealm} from './../../data/creators'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'

const newObjId = "new"

const dumbRealm = CreateRealm(newObjId, '')

const createState = function(isOpen, isStatic, isEdit, currentRealm){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    currentRealm: currentRealm
  }
}

const defaultState = function(){
  return createState(false, true, false, dumbRealm)
}

export class RealmModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('realm-modal', 'open', function(state, currentRealm){
      this.setState(createState(true, currentRealm.id==newObjId, currentRealm.id==newObjId, currentRealm))
    }.bind(this))

    registerEvent('realm-modal', 'close', function(){
      this.setState(defaultState())
    }.bind(this))

    registerReaction('realm-modal', 'realm-rep', 'created', ()=>{
      fireEvent('realm-modal', 'close')
    })

  }

  okHandler(){
    if(this.state.currentRealm.title != ''){
      if(this.state.currentRealm.id==newObjId){
        fireEvent('realm-rep', 'create', [this.state.currentRealm])
      } else {
        console.log('realm modify called')
        fireEvent('realm-rep', 'modify', [this.state.currentRealm])
      }
    } else {
      this.props.isTitleValid = false
      this.setState({})
    }
  }

  handleNameVal(e){
    this.state.currentRealm.title = e.target.value;
    this.setState({})
  }

  render(){
    return <CommonModal isOpen = {this.state.isOpen} okHandler = {this.okHandler} cancelHandler={()=>fireEvent('realm-modal', 'close', [])} title={"Realm"} >
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent('realm-rep', 'delete', [this.state.currentRealm.id])}>
          <form>
            <FormGroup controlId="formBasicText">
              <ControlLabel>Title</ControlLabel>
              {this.state.mode.isEdit? <FormControl
                type="text"
                value={this.state.currentRealm.title}
                placeholder="Enter title"
                onChange={this.handleNameVal}/>
              : <FormControl.Static>{this.state.currentRealm.title}</FormControl.Static>}
              {this.state.isTitleValid!=null && !this.state.isTitleValid? <Alert bsStyle="danger"><strong>Title</strong> must not be empty</Alert>: null}
            </FormGroup>
          </form>
        </CommonCrudeTemplate>
      </CommonModal>
  }
}
