import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem, ButtonGroup} from 'react-bootstrap'
import {CreateTarget, CreateRealm} from './../../data/creators'
import {TargetModal} from './target-modal'
import {RealmModal} from './realm-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {TreeComponent} from './../tree-component'

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {editTree: false}

    registerReaction('targets-frame', 'targets-dao', 'targets-received', ()=>this.setState({}))
    registerReaction('targets-frame', 'realms-dao', ['realms-received', 'change-current-realm', 'realm-created'], ()=>this.setState({}))

    registerReaction('targets-frame', 'targets-dao',
            ['targets-received', 'replace-target', 'target-created', 'target-deleted', 'target-modified', 'targets-list-modified', 'draggable-add-as-child'], ()=>this.setState({}))

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
            {realmsUI(this)}
          </ListGroup>
        </div>
      </div>
    )
  }
}

const realmsUI = function(component){
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
                <input type="radio" autocomplete="off" checked={isCurrentRealm?"checked":null} style={{marginRight:'5px'}} />
                {realms[realmId].title}
              </h4>
            </div>
            <div>
              {isCurrentRealm?targetsUI(component):null}
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

const targetsUI = function(component){
  return <div>
          <div>{getControlButtonsForTargets(component)}</div>
          <div>{targetsUIlist(component)}</div>
    </div>
}

const getControlButtonsForTargets = function(component){
  const result = []
  result.push(<Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao','currentRealm').id)])}>
              {createNewTargetButtonTitle}
            </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const targetsUIlist = function(component){
      var result = 'Loading...'
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        component.state.allNodes = viewStateVal('targets-dao', 'targets')[viewStateVal('realms-dao', 'currentRealm').id]
        result = <TreeComponent isEdit={component.state.isEdit}
                  nodes={viewStateVal('targets-dao', 'targets')[viewStateVal('realms-dao', 'currentRealm').id]}
                  viewCallback = {(target)=>targetUI(component, target)}
                  onDropCallback = {(alteredList)=>fireEvent('targets-dao', 'modify-list', [alteredList])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
      }
      return result
}

const targetUI = function(component, target){
  const styleForLinks = {color:'darkgreen'}
  return <div style={target.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    <a href="#" onClick={()=>fireEvent('target-modal', 'open', [target])} style={styleForLinks}>{target.title}</a>
                    <a href="#" style = {{marginLeft:'3px', color:'darkgreen'}} onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), target])}>
                      {addNewTargetTitle}
                    </a>
                </div>
}
