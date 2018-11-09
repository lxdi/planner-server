import {taskModalHeaderTitle, meanDropDownTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CreateTask, AddTask} from './../../data/tasks-dao'
import {AllMeans} from './../../data/means-dao'
import {ButtonToolbar,  DropdownButton, MenuItem, FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import {CommonCrudeTemplate} from './../common-crud-template'

// const defaultState = function(){
//   return {
//     currentTask: CreateTask(0, '', []),
//     editing: {isEdit: true, editAbility: false}
//   }
// }

// Props:
// isOpen
// currentTask
// closeCallback
// mode: {isStatic: true, isEdit: false}
export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    //this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.cancelHandler = this.cancelHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);
    this.selectMeanHandler = this.selectMeanHandler.bind(this);
    this.deleteHandler = this.deleteHandler.bind(this);
  }

  okHandler(){
    //this.setState(defaultState())
    if(this.props.currentTask.id==0)
      this.props.closeCallback("create", this.props.currentTask);
    else
      this.props.closeCallback("update", this.props.currentTask);
  }

  cancelHandler(){
    //this.setState(defaultState())
    this.props.closeCallback();
  }

  handleNameVal(e){
    this.props.currentTask.title = e.target.value;
    this.setState({});
  }

  selectMeanHandler(mean, e){
    this.props.currentTask.mean = mean;
    this.setState({});
  }

  deleteHandler(){
    this.props.closeCallback("delete", this.props.currentTask);
  }

  render(){
    return (
      <CommonModal isOpen = {this.props.isOpen} okHandler = {this.okHandler} cancelHandler = {this.cancelHandler} title={taskModalHeaderTitle}>
        <CommonCrudeTemplate editing = {this.props.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={this.deleteHandler}>
          <form>
            <FormGroup controlId="formBasicText">
              <ControlLabel>Title</ControlLabel>
                {this.props.mode.isEdit? <FormControl
                                  type="text"
                                  value={this.props.currentTask.title}
                                  placeholder="Enter title"
                                  onChange={this.handleNameVal}/>
                                : <FormControl.Static>{this.props.currentTask.title}</FormControl.Static>}
            </FormGroup>
          </form>
          {this.props.mode.isEdit? <div>
                        <ButtonToolbar>
                          <DropdownButton bsSize="small" title={meanDropDownTitle} id="dropdown-size-small" onSelect={this.selectMeanHandler}>
                            {availableMeansUI()}
                          </DropdownButton>
                      </ButtonToolbar>
                  </div>
            : null}
            {
              this.props.currentTask.mean!=null?
                this.props.currentTask.mean.toString(): ''
            }
      </CommonCrudeTemplate>
      </CommonModal>
    )
  }
}

const availableMeansUI = function(){
  return AllMeans().map((mean) =>
    <MenuItem eventKey={mean}>{mean.toString()}</MenuItem>
  );
}
