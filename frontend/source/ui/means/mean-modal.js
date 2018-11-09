import {meanModalHeaderTitle, targetsDropDownTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {AllTargets} from './../../data/targets-dao'
import {AllMeans, CreateMean} from './../../data/means-dao'
import {CommonCrudeTemplate} from './../common-crud-template'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl} from 'react-bootstrap'


// Props:
// isOpen
// currentMean
// closeCallback
// mode: {isStatic: true, isEdit: false}
export class MeanModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {};
    this.okHandler = this.okHandler.bind(this);
    this.cancelHandler = this.cancelHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);
    this.selectTargetHandler = this.selectTargetHandler.bind(this);
    this.deleteHandler = this.deleteHandler.bind(this);

  }

  okHandler(){
    this.props.closeCallback(this.props.currentMean.id==0? 'create': 'modify', this.props.currentMean);
  }

  cancelHandler(){
    //this.setState(defaultState())
    this.props.closeCallback();
  }

  handleNameVal(e){
    this.props.currentMean.title = e.target.value;
    this.setState({});
  }

  deleteHandler(){
    this.props.closeCallback('delete', this.props.currentMean);
  }

  selectTargetHandler(target, e){
    this.props.currentMean.targets.push(target)
    this.setState({});
  }

  render(){
    return (
      <CommonModal isOpen = {this.props.isOpen} okHandler = {this.okHandler} cancelHandler = {this.cancelHandler} title={meanModalHeaderTitle} >
        <CommonCrudeTemplate editing = {this.props.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={this.deleteHandler}>
            <form>
              <FormGroup controlId="formBasicText">
                <ControlLabel>Title</ControlLabel>
              {this.props.mode.isEdit? <FormControl
                                type="text"
                                value={this.props.currentMean.title}
                                placeholder="Enter title"
                                onChange={this.handleNameVal}/>
                              : <FormControl.Static>{this.props.currentMean.title}</FormControl.Static>}
              </FormGroup>
            </form>
            {this.props.mode.isEdit? <div>
                          <ButtonToolbar>
                          <DropdownButton bsSize="small" title={targetsDropDownTitle} id="dropdown-size-small" onSelect={this.selectTargetHandler}>
                            {availableTargetsUI()}
                          </DropdownButton>
                        </ButtonToolbar>
                    </div>
              : null}
            <div>
              {relatedTargetsUI(this.props.currentMean.targets)}
            </div>
        </CommonCrudeTemplate>
      </CommonModal>
    )
  }
}

const availableTargetsUI = function(){
  return AllTargets().map(function(target){
    return <MenuItem eventKey={target}>{target.toString()}</MenuItem>
  })
}

const relatedTargetsUI = function(targets){
  return targets.map((target) =>
    <li>{target.toString()}</li>
  );
}
