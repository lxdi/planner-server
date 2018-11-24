import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem} from 'react-bootstrap'
import {CreateTarget, CreateRealm} from './../../data/creators'
import {TargetModal} from './target-modal'
import {RealmModal} from './realm-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

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
        <TargetModal/>
        <RealmModal/>
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
  if(viewStateVal('realms-dao', 'realms')!=null){
    if(viewStateVal('targets-dao', 'targets')!=null){
      const result = []
      const realms = viewStateVal('realms-dao','realms')
      for(var realmId in realms){
        const realmidConst = realmId
        const isCurrentRealm = realms[realmidConst]==viewStateVal('realms-dao','currentRealm')
        result.push(<ListGroupItem key={"realm_"+realmidConst+(realms[realmidConst]==viewStateVal('realms-dao','currentRealm')?"_current":"_notcurrent")}>
            <div>
              <h4 onClick={()=>fireEvent('realms-dao', 'change-current-realm', [realms[realmidConst]])}>
                <input type="radio" autocomplete="off"
                  checked={isCurrentRealm?"checked":null}/>
                {realms[realmId].title}
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
      <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao','realms')[realmId].id)])}>
                  {createNewTargetButtonTitle}
                </Button>
              <div>{targetsUIlist(realmId)}</div>
    </div>
}

const targetsUIlist = function(realmId){
    return viewStateVal('targets-dao', 'targets').map(function(target){
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
        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao','realms')[realmId].id), target])}>
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
