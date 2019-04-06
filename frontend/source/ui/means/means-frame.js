import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CreateMean} from './../../data/creators'
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from 'absevent'
import {TreeComponent} from './../components/tree-component'
import {SubjectModal} from './subject-modal'
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

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao',
            ['means-received', 'replace-mean', 'mean-created', 'mean-deleted', 'mean-modified', 'means-list-modified', 'draggable-add-as-child', 'hide-children-changed'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        {viewStateVal('realms-dao', 'currentRealm')!=null?
          <div style={{'margin-bottom': '3px'}}>
            {getControlButtons(this)}
            <MeanModal/>
            <SubjectModal/>
            <TaskModal/>
          </div>:null}
          {meansUIlist(this)}
      </div>
    )
  }
}

const getControlButtons = function(component){
  const result = []
  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, [])])}>
                {createNewMeanButtonTitle}
              </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const meansUIlist = function(component){
  if(viewStateVal('means-dao', 'means')!=null){
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        return <TreeComponent isEdit={component.state.isEdit}
                  nodes={viewStateVal('means-dao', 'means')[viewStateVal('realms-dao', 'currentRealm').id]}
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
  return <div style={mean.parentid!=null?{borderLeft:'1px solid grey', paddingLeft:'3px'}:null}>
                    {hideShowChildrenControlUI(component, mean)}
                    <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])}>
                        {markDraggableMeanTitle(mean)}
                    </a>
                    <a href="#" style = {{marginLeft:'3px'}} onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), mean])}>
                      {addNewMeanTitle}
                    </a>
                    <span style={{color: 'green', fontSize:'8pt'}}> {targetsTagsString(mean)}</span>
                </div>
}

const markDraggableMeanTitle = function(mean){
  if(viewStateVal('means-dao', 'draggableMean')==mean){
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
    targetsString = targetsString +divisor+viewStateVal('targets-dao', 'targets')[mean.realmid][mean.targetsIds[indx]];
  }
  return targetsString
}
