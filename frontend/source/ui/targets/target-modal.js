import {targetModalHeaderTitle, deleteButton, editButton, viewButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {AddTarget, CreateTarget, CreateTargetWithIncrementedId} from './../../data/targets-dao'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'

// Props:
// isOpen
// currentTarget
// closeCallback
// mode: {isStatic: true, isEdit: false}
export class TargetModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {};
    this.okHandler = this.okHandler.bind(this);
    this.cancelHandler = this.cancelHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);
    this.deleteHandler = this.deleteHandler.bind(this);
  }

  okHandler(){
    if(this.props.currentTarget.title != ''){
      this.props.closeCallback(this.props.currentTarget.id==0? 'create': 'modify', this.props.currentTarget);
    } else {
      this.props.isTitleValid = false
      this.setState({})
    }
  }

  cancelHandler(){
    this.props.closeCallback();
  }

  handleNameVal(e){
    this.props.currentTarget.title = e.target.value;
    this.setState({})
  }

  deleteHandler(){
    this.props.closeCallback('delete', this.props.currentTarget);
  }

  render(){
    return (
      <CommonModal isOpen = {this.props.isOpen} okHandler = {this.okHandler} cancelHandler={this.cancelHandler} title={targetModalHeaderTitle} >
        <CommonCrudeTemplate editing = {this.props.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={this.deleteHandler}>
          <form>
            <FormGroup controlId="formBasicText">
              <ControlLabel>Title</ControlLabel>
              {this.props.mode.isEdit? <FormControl
                type="text"
                value={this.props.currentTarget.title}
                placeholder="Enter title"
                onChange={this.handleNameVal}/>
              : <FormControl.Static>{this.props.currentTarget.title}</FormControl.Static>}
              {this.state.isTitleValid!=null && !this.state.isTitleValid? <Alert bsStyle="danger"><strong>Title</strong> must not be empty</Alert>: null}
            </FormGroup>
          </form>
        </CommonCrudeTemplate>
      </CommonModal>
    )
  }
}
