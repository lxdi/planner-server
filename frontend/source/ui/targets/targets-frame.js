import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem} from 'react-bootstrap'
import {CreateTarget, TargetsState} from './../../data/targets-dao'
import {CreateRealm, RealmsState} from './../../data/realms-dao'
import {TargetModal} from './target-modal'
import {RealmModal} from './realm-modal'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}

    registerReaction('targets-frame', 'targets-dao', ['target-created', 'target-deleted', 'target-modified'], function(){
      fireEvent('target-modal', 'close')
      this.setState({})
    }.bind(this))

    registerReaction('targets-frame', 'realms-dao', ['realms-created'], function(){
      fireEvent('realm-modal', 'close')
      this.setState({})
    }.bind(this))

    registerReaction('targets-frame', 'targets-dao', 'targets-received', ()=>this.setState({}))
    registerReaction('targets-frame', 'realms-dao', 'realms-received', ()=>this.setState({}))

    // AllTargets(function(){
    //   this.setState({});
    // }.bind(this))
  }

  render(){
    return(
      <div>
        <div style={{'margin-bottom': '3px'}}>
          <TargetModal/>
          <RealmModal/>
        </div>
        <div>
          <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('realm-modal', 'open', [CreateRealm(0, '')])}>
            Create Realm
          </Button>
          <ListGroup>
            {realmsUI()}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const realmsUI = function(){
  if(RealmsState.realmsLoaded){
    const result = []
    for(var realmId in RealmsState.realms){
      const realmidConst = realmId
      result.push(<ListGroupItem>
          <div>
            {RealmsState.realms[realmId].title}
            <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', RealmsState.realms[realmidConst].id)])}>
              {createNewTargetButtonTitle}
            </Button>
          </div>
          <div>{targetsUIlist(realmId)}</div>
        </ListGroupItem>)
    }
    return result
  } else {
    fireEvent('realms-dao', 'realms-request', [])
    return null
  }
}

const targetsUIlist = function(realmId){
  if(TargetsState.targetsLoaded){
    return TargetsState.targets.map(function(target){
          return <ListGroupItem>
            {targetUI(target, realmId, 20)}
          </ListGroupItem>
    }, function(target){
      return target.parentid == null && target.realmid == realmId
    })
  } else {
    fireEvent('targets-dao', 'targets-request', [])
    return null
  }
}

const targetUI = function(target, realmId, offset){
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [target])}> {target.toString()} </a><span/>
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', RealmsState.realms[realmId].id), target])}>
            {addNewTargetTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {target.children.map(function(childTarget){
            return <li>
              {targetUI(childTarget, realmId, offset + 10)}
            </li>
        })}
      </div>
    </div>
  )
}
