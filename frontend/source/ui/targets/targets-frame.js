import {createNewTargetButtonTitle, addNewTargetTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, FormGroup, ControlLabel, FormControl, ListGroup, ListGroupItem, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {TreeComponent} from './../components/tree-component'

import {CreateTarget, CreateRealm} from './../../data/creators'
import {TargetModal} from './target-modal'
import {RealmModal} from './realm-modal'

const newObjId = 'new'
const realmRep = 'realm-rep'
const targetRep = 'target-rep'
const repObjects = 'objects'
const currentRealm = 'currentRealm'
const indexByRealmid = 'index-by-realmid'

export class TargetsFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {editTree: false}
    registerEvent('targets-frame', 'update', ()=>this.setState({}))
    registerReaction('targets-frame', realmRep, ['all-response', 'change-current-realm', 'created'], ()=>this.setState({}))

    registerReaction('targets-frame', targetRep,
            ['all-response', 'created', 'deleted', 'updated',
            'replace-target', 'targets-list-modified',
            'draggable-add-as-child', 'highlight', 'highlight-clean', 'clear-rep'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        <TargetModal/>
        <RealmModal/>
        <div>
          <Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('realm-modal', 'open', [CreateRealm(newObjId, '')])}>
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
  const realmsMap = chkSt(realmRep,repObjects)

  if (realmsMap == null) {
    fireEvent(realmRep, 'all-request', [])
    return null
  }

  const realms = Object.values(realmsMap)
  realms.sort((r1, r2) => r1.priority - r2.priority)
  const targets = chkSt(targetRep, repObjects)

  if (targets == null) {
    fireEvent(targetRep, 'all-request', [])
    return null
  }

  const result = []

  for(var i in realms){
    const realm = realms[i]

    result.push(<ListGroupItem key={"realm_"+realm.id+(realm.current?"_current":"_notcurrent")}>
            <div>
              <h4 onClick={()=>fireEvent(realmRep, 'change-current-realm', [realm])}>
                <input type="radio" autocomplete="off" checked={realm.current?"checked":null} style={{marginRight:'5px'}} />
                {realm.title}
              </h4>
            </div>
            <div>
              {realm.current?targetsUI(component):null}
            </div>
          </ListGroupItem>)
  }
  return result
}

const targetsUI = function(component){
  return <div>
          <div>
            <div style={{display:'inline-block'}}>{getControlButtonsForTargets(component)}</div>
            {currentHighlightedTargetUI()}
          </div>
          <div>{targetsUIlist(component)}</div>
    </div>
}

const currentHighlightedTargetUI = function(){
  if(chkSt(targetRep, 'highlight')!=null){
    return <div style={{display:'inline-block', marginLeft:'5px', padding:'3px', border:'1px solid lightgrey'}}>
       {chkSt(targetRep, 'highlight').title}
       <a href='#' onClick={()=>fireEvent(targetRep, 'highlight-clean')}>X</a>
     </div>
  } else {
    return null;
  }
}

const getControlButtonsForTargets = function(component){
  const result = []
  result.push(<Button bsStyle="success" bsSize="xsmall" onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(newObjId, '', chkSt(realmRep,currentRealm).id), null])}>
              {createNewTargetButtonTitle}
            </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const targetsUIlist = function(component){
      var result = 'Loading...'
      const targets = chkSt(realmRep, currentRealm)
      if(targets!=null){
        result = <TreeComponent isEdit={component.state.isEdit}
                  nodes={chkSt(targetRep, indexByRealmid)[chkSt(realmRep, currentRealm).id]}
                  viewCallback = {(target)=>targetUI(component, target)}
                  onDropCallback = {(alteredList)=>fireEvent(targetRep, 'modify-list', [alteredList])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
      }
      return result
}

const targetUI = function(component, target){
  const styleForLinks = {color:'darkgreen'}
  return <div style={target.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    <div>
                      <div>
                        <a href="#" onClick={()=>fireEvent('target-modal', 'open', [target])} style={styleForLinks}>
                          {target.title}
                        </a>
                        {highlightButtonUI(target)}
                        <a href="#" style = {{marginLeft:'3px', color:'darkgreen'}}
                                    onClick={()=>fireEvent('target-modal', 'open', [CreateTarget(newObjId, '', chkSt(realmRep, currentRealm).id, target.id)])}>
                          {addNewTargetTitle}
                        </a>
                      </div>
                      {targetMetadataUI(component, target)}
                    </div>
                </div>
}

const targetMetadataUI = function(comp, target){
  const styleForMetadata = {display:'inline-block', marginRight:'3px', paddingRight:'3px', borderRight:'1px solid lightgrey'}
  if(!checkTargetIfParent(target)){
    return <div style={{fontSize:'9pt'}}>
      <div style={styleForMetadata}><span style={{fontWeight:'bold', color:'blue'}}>Means:</span> {target.meansCount}</div>
      <div style={styleForMetadata}><span style={{fontWeight:'bold', color:'orange'}}>layers:</span> {target.layersAssignedCount}{target.layersCount}</div>
      <div style={styleForMetadata}><span style={{fontWeight:'bold', color:'green'}}>{target.finishDate!=null?target.finishDate:null}</span></div>
    </div>
  } else {
    return null
  }
}

const checkTargetIfParent = function(target){
  for(var idx in chkSt(targetRep, repObjects)[target.realmid]){
    if(chkSt(targetRep, repObjects)[target.realmid][idx].parentid==target.id){
      return true
    }
  }
  return false
}

const highlightButtonUI = function(target){
  if(chkSt(targetRep, 'highlight')==null && !checkTargetIfParent(target)){
    return <a href="#" onClick={()=>fireEvent(targetRep, 'highlight', [target])}>
                              (highlight)
                            </a>
  } else {
    return null
  }
}
