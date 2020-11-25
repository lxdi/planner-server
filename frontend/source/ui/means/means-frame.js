import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {TreeComponent} from './../components/tree-component'

import {CreateMean} from './../../data/creators'
import {MeanModal} from './mean-modal'
import {TaskModal} from './task-modal'

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {isEdit: false}

    registerEvent('means-frame', 'update', ()=>this.setState({}))

    registerReaction('means-frame', 'targets-dao', 'target-deleted', (state, target)=>{
      fireEvent('means-dao', 'delete-depended-means', [target])
      this.setState({})
    })

    registerReaction('means-frame', 'targets-dao', ['highlight', 'highlight-clean'], ()=>this.setState({}))

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao',
            ['means-received', 'replace-mean', 'mean-created',
            'mean-deleted', 'mean-modified', 'means-list-modified',
            'draggable-add-as-child', 'hide-children-changed'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        {chkSt('realms-dao', 'currentRealm')!=null?
          <div style={{'margin-bottom': '3px'}}>
            {getControlButtons(this)}
            <MeanModal/>
            <TaskModal/>
          </div>:null}
          {meansUIlist(this)}
      </div>
    )
  }
}

const getControlButtons = function(component){
  const result = []
  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', chkSt('realms-dao', 'currentRealm').id, [])])}>
                {createNewMeanButtonTitle}
              </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const meansUIlist = function(component){
  if(chkSt('means-dao', 'means')!=null){
      if(chkSt('realms-dao', 'currentRealm')!=null){
        return <TreeComponent isEdit={component.state.isEdit}
                  nodes={chkSt('means-dao', 'means')[chkSt('realms-dao', 'currentRealm').id]}
                  viewCallback = {(mean)=>meanUI(component, mean)}
                  onDropCallback = {(alteredList)=>{fireEvent('means-dao', 'modify-list', [alteredList]); fireEvent('means-dao', 'remove-draggable')}}
                  onDragStartCallback = {(mean)=>fireEvent('means-dao', 'add-draggable', [mean])}
                  rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                  shiftpx={15}
                  />
      }
      return ''
    }
  return 'Loading...'
}

const meanUI = function(component, mean){
  var meanLinkStyle = {}
  if(chkSt('targets-dao', 'highlight')!=null){
    if(!isMeanAssignedToTarget(chkSt('targets-dao', 'highlight'), mean)){
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
                    <a href="#" style = {{marginLeft:'3px'}} onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', chkSt('realms-dao', 'currentRealm').id, []), mean])}>
                      {addNewMeanTitle}
                    </a>
                    <span style={{color: 'green', fontSize:'8pt'}}> {targetsTagsString(mean)}</span>
                </div>
}

const markDraggableMeanTitle = function(mean){
  if(chkSt('means-dao', 'draggableMean')==mean){
    return <strong>{mean.title}</strong>
  } else {
    return mean.title
  }
}

const hideShowChildrenControlUI = function(component, mean){
  return <a href="#" style = {{marginRight:'3px'}} onClick={()=>{
      mean.hideChildren = !mean.hideChildren
      fireEvent('means-dao', 'hide-children', [mean])
    }}>
    {mean.hideChildren==null || (mean.hideChildren!=null && mean.hideChildren==false)?'-':'+'}
  </a>
}

const targetsTagsString = function(mean){
  var targetsString = '';
  var divisor = ' #';
  for(var indx in mean.targetsIds){
    targetsString = targetsString +divisor+chkSt('targets-dao', 'targets')[mean.realmid][mean.targetsIds[indx]];
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
