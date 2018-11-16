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
    registerReaction('targets-frame', 'realms-dao', ['realms-received', 'change-current-realm'], ()=>this.setState({}))
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
            Create New Realm
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
    if(TargetsState.targetsLoaded){
      const result = []
      for(var realmId in RealmsState.realms){
        const realmidConst = realmId
        const isCurrentRealm = RealmsState.realms[realmidConst]==RealmsState.currentRealm
        result.push(<ListGroupItem key={"realm_"+realmidConst+(RealmsState.realms[realmidConst]==RealmsState.currentRealm?"_current":"_notcurrent")}>
            <div>
              <h4 onClick={()=>fireEvent('realms-dao', 'change-current-realm', [RealmsState.realms[realmidConst]])}>
                <input type="radio" autocomplete="off"
                  checked={isCurrentRealm?"checked":null}/>
                {RealmsState.realms[realmId].title}
              </h4>
            </div>
            <div>
              {isCurrentRealm?targetsUI(realmidConst):null}
            </div>
          </ListGroupItem>)
      }
      return result
    } else {
      fireEvent('targets-dao', 'targets-request', [])
      return null
    }
  } else {
    fireEvent('realms-dao', 'realms-request', [])
    return null
  }
}

const targetsUI = function(realmId){
  return <div>
      <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', RealmsState.realms[realmId].id)])}>
                  {createNewTargetButtonTitle}
                </Button>
              <div>{targetsUIlist(realmId)}</div>
    </div>
}

const targetsUIlist = function(realmId){
    return TargetsState.targets.map(function(target){
          return <ListGroupItem>
            {targetUI(target, realmId, 20)}
          </ListGroupItem>
    }, function(target){
      return target.parentid == null && target.realmid == realmId
    })
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
