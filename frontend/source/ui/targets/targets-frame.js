import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem} from 'react-bootstrap'
import {AllTargets, CreateTarget} from './../../data/targets-dao'
import {TargetModal} from './target-modal'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}

    registerReaction('targets-frame', 'targets-dao', ['target-created', 'target-deleted', 'target-modified'], function(){
      fireEvent('target-modal', 'close')
      this.setState({})
    }.bind(this))

    AllTargets(function(){
      this.setState({});
    }.bind(this))
  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '')])}>
            {createNewTargetButtonTitle}
          </Button>
          <TargetModal/>
        </div>
        <div>
          <ListGroup>
            {targetsUIlist()}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const targetsUIlist = function(){
  return AllTargets().map(function(target){
        return <ListGroupItem>
          {targetUI(target, 20)}
        </ListGroupItem>
  }, function(target){
    return target.parentid == null
  })
}

const targetUI = function(target, offset){
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [target])}> {target.toString()} </a><span/>
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, ''), target])}>
            {addNewTargetTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {target.children.map(function(childTarget){
            return <li>
              {targetUI(childTarget, offset + 10)}
            </li>
        })}
      </div>
    </div>
  )
}
