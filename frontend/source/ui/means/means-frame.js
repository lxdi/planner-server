import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {TreeComponent} from './../components/tree-component'

import {CreateMean} from './../../data/creators'
import {MeanModal} from './modal/mean-modal'
import {TaskModal} from './modal/task-modal'

const newId = 'new'
const realmRepName = 'realm-rep'
const targetRepName = 'target-rep'
const currentRealm = 'currentRealm'
const indexByRealmid = 'index-by-realmid'

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {isEdit: false}

    registerEvent('means-frame', 'update', ()=>this.setState({}))

    registerReaction('means-frame', realmRepName, ['all-response', 'change-current-realm'], ()=>{this.setState({})})
    registerReaction('means-frame', targetRepName, ['highlight', 'highlight-clean'], ()=>this.setState({}))

    registerReaction('means-frame', 'mean-rep',
            ['all-response', 'created', 'deleted', 'updated',
              'replace-mean', 'repositioned',
            'draggable-add-as-child', 'hide-children-changed'], ()=>this.setState({}))
  }

  render(){
    if(chkSt(realmRepName, currentRealm)==null){
      return null
    }

    return(
      <div>
          <div style={{'margin-bottom': '3px'}}>
            {getControlButtons(this)}
            <MeanModal/>
            <TaskModal/>
          </div>
          {meansUIlist(this)}
      </div>
    )
  }
}

const getControlButtons = function(component){
  const result = []
  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(newId, '', chkSt(realmRepName, currentRealm).id)])}>
                {createNewMeanButtonTitle}
              </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const meansUIlist = function(component){

  if (chkSt('mean-rep', 'objects')==null) {
      fireEvent('mean-rep', 'all-request')
      return 'Loading...'
  }

  const curRealm = chkSt(realmRepName, currentRealm)

  if (curRealm==null){
    return ''
  }

  return <TreeComponent isEdit={component.state.isEdit}
                  nodes={chkSt('mean-rep', indexByRealmid)[curRealm.id]}
                  viewCallback = {(mean)=>meanUI(component, mean)}
                  onDropCallback = {(alteredList)=>{fireEvent('mean-rep', 'reposition', [alteredList])}}
                  onDragStartCallback = {(mean, e)=> fireEvent('drag-n-drop', 'put', ['assign-mean', mean])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
}

const meanUI = function(component, mean){
  var meanLinkStyle = {}

  if(chkSt(targetRepName, 'highlight')!=null){
    if(!isMeanAssignedToTarget(chkSt(targetRepName, 'highlight'), mean)){
      meanLinkStyle = {color:'grey', fontSize:'9pt'}
    } else {
      meanLinkStyle = {fontSize:'12pt'}
    }
  }

  return <div style={mean.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    {hideShowChildrenControlUI(component, mean)}
                    <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])} style={meanLinkStyle}>
                        {markDraggableMeanTitle(mean)}
                    </a>
                    <a href="#" style = {{marginLeft:'3px'}} onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(newId, '', chkSt(realmRepName, currentRealm).id, mean.id)])}>
                      {addNewMeanTitle}
                    </a>
                    <span style={{color: 'green', fontSize:'8pt'}}> {targetsTagsString(mean)}</span>
                </div>
}

const markDraggableMeanTitle = function(mean){
  if(chkSt('mean-rep', 'draggableMean')==mean){
    return <strong>{mean.title}</strong>
  } else {
    return mean.title
  }
}

const hideShowChildrenControlUI = function(component, mean){
  return <a href="#" style = {{marginRight:'3px'}} onClick={()=>{
      mean.hideChildren = !mean.hideChildren
      fireEvent('mean-rep', 'hide-children', [mean])
    }}>
    {mean.hideChildren==null || (mean.hideChildren!=null && mean.hideChildren==false)?'-':'+'}
  </a>
}

const targetsTagsString = function(mean){
  var targetsString = '';
  var divisor = ' #';
  for(var indx in mean.targetsIds){
    targetsString = targetsString +divisor+chkSt(targetRepName, 'targets')[mean.realmid][mean.targetsIds[indx]];
  }
  return targetsString
}

const isMeanAssignedToTarget = function(target, mean){
  for(var id in mean.targetsIds){
    if(mean.targetsIds[id]==target.id){
      return true
    }
  }
  return false
}
