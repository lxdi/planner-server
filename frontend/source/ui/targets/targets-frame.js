import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem} from 'react-bootstrap'
import {AllTargets, AddTarget, DeleteTargetById, UpdateTarget, CreateTarget} from './../../data/targets-dao'
import {TargetModal} from './target-modal'

const defaultState = function(){
  return {
    isTargetModalOpen:false,
    parent: null,
    currentTarget: CreateTarget(0, ''),
    modalMode: {isStatic: true, isEdit: false}
  }
}

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = defaultState()
    this.targetModalOpen = this.targetModalOpen.bind(this);
    this.closeModalHandler = this.closeModalHandler.bind(this);
    this.targetModalCreateNewOpen = this.targetModalCreateNewOpen.bind(this);
    this.newTargetModalOpen = this.newTargetModalOpen.bind(this);

    //this.state.targets = []
    AllTargets(function(){
      //this.state.targets = fullfilled;
      this.setState({});
    }.bind(this))
  }

  //Create new target on root level, handler for button Create New
  targetModalCreateNewOpen(){
    this.setState({
      isTargetModalOpen:true,
      currentTarget: CreateTarget(0, ''),
      modalMode: {isStatic: true, isEdit: true}
    })
  }

  // Create new target as children to an existing one
  newTargetModalOpen(parentTarget){
    this.setState({
      isTargetModalOpen:true,
      currentTarget: CreateTarget(0, ''),
      parent: parentTarget,
      modalMode: {isStatic: true, isEdit: true}
    })
  }

  //Open existing target in modal
  targetModalOpen(target){
    this.setState({
      isTargetModalOpen:true,
      currentTarget: target,
      parent:null,
      modalMode: {isStatic: false, isEdit: false}
    })
  }

  closeModalHandler(eventtype, target){
    if(eventtype == 'create'){
      const curtarget = target
      AddTarget(target, this.state.parent, function(){
        this.setState({})
      }.bind(this))
    }
    if(eventtype == 'modify'){
      UpdateTarget(target, function(){
        this.setState({})
      }.bind(this))
    }
    if(eventtype == 'delete'){
      DeleteTargetById(target.id, function(){
        this.setState({})
      }.bind(this))
    }
    this.setState(defaultState())
  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="success" bsSize="xsmall" onClick={this.targetModalCreateNewOpen}>
            {createNewTargetButtonTitle}
          </Button>
          {<TargetModal isOpen={this.state.isTargetModalOpen} closeCallback={this.closeModalHandler} currentTarget={this.state.currentTarget} mode={this.state.modalMode}/>}
        </div>
        <div>
          <ListGroup>
            {targetsUIlist(this.targetModalOpen, this.newTargetModalOpen, this.state.targets)}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const targetsUIlist = function(editOpenHandler, addHandler, targets){
  return AllTargets().map(function(target){
        return <ListGroupItem>
          {targetUI(target, editOpenHandler, addHandler, 20)}
        </ListGroupItem>
  }, function(target){
    return target.parentid == null
  })
}

const targetUI = function(target, editOpenHandler, addHandler, offset){
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={editOpenHandler.bind(this, target)}> {target.toString()} </a><span/>
      <a href="#" onClick={addHandler.bind(this, target)}>
          {addNewTargetTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {target.children.map(function(childTarget){
            return <li>
              {targetUI(childTarget, editOpenHandler, addHandler, offset + 10)}
            </li>
        })}
      </div>
    </div>
  )
}
