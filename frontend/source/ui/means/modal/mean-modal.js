import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt, registerReactionCombo} from 'absevents'
import {iterateLLfull, iterateTree} from 'js-utils'

import {DataConstants} from '../../../data/data-constants'
import {LayersGroup} from './layers-group'
import {CreateMean, CreateTarget, CreateLayer, CreateTask} from './../../../data/creators'
import {meanModalHeaderTitle, targetsDropDownTitle} from './../../../titles'
import {CommonModal} from './../../common/common-modal'
import {CommonCrudeTemplate} from './../../common/common-crud-template'
import {isValidMean} from '../../../utils/mean-validator'
import {addNewLayerToMean} from '../../../data/mean-loader'

const meanRep = 'mean-rep'
const layerRep = 'layer-rep'
const taskRep = 'task-rep'
const objMapName = 'objects'
const currentRealm = 'currentRealm'
const indexByMean = 'index-by-mean'
const byMeanRequest = 'by-mean-request'
const byMeanResponse = 'by-mean-response'

const createState = function(isOpen, isStatic, isEdit, currentMean){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    currentMean: currentMean
  }
}

const defaultState = function(){
  return createState(false, true, false, CreateMean(DataConstants.newId, '', null, []))
}

export class MeanModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('mean-modal', 'open', function(stateSetter, currentMean){
      this.setState(createState(true, currentMean.id==DataConstants.newId, currentMean.id==DataConstants.newId, currentMean))
      return currentMean
    }.bind(this))

    registerEvent('mean-modal', 'close', function(){
      this.state.currentMean.isFull = false
      fireEvent(layerRep, 'clean')
      fireEvent(taskRep, 'clean')
      fireEvent(DataConstants.topicRep, 'clean')
      fireEvent(DataConstants.tasktestingRep, 'clean')
      this.setState(defaultState())
    }.bind(this))

    registerEvent('mean-modal', 'remove-target')
    registerReaction('means-modal', meanRep, ['deleted', 'updated', 'created'], ()=>this.setState(defaultState()))
    registerReaction('means-modal', meanRep, ['got-full'], ()=>this.setState({}))
    registerReaction('mean-modal', 'task-modal', 'close', ()=>this.setState({}))

  }

  okHandler(){
    if(this.state.currentMean.id==DataConstants.newId){
      fireEvent(meanRep, 'create', [this.state.currentMean])
    } else {
      fireEvent(meanRep, 'update', [this.state.currentMean])
    }
    this.state.currentMean.isFull=false
  }

  handleNameVal(e){
    this.state.currentMean.title = e.target.value;
    this.setState({});
  }

  render(){
    return renderUI(this)
  }
}


const renderUI = function(component){
  component.state.alerts = []
  return <CommonModal
            isOpen = {component.state.isOpen}
            okHandler = {isValidMean(component.state.alerts, component.state.currentMean)?component.okHandler:null}
            cancelHandler = {()=>fireEvent('mean-modal', 'close', [])}
            title={getTitle(component.state)}
            styleClass='mean-modal-style'>

            {modalBody(component)}
    </CommonModal>
}

const getTitle = function(state){
  if(state.currentMean!=null && state.currentMean.id != DataConstants.newId){
    return state.currentMean.title
  }
  return 'Create new Mean'
}

const modalBody = function(component){
    if(!prepareMean(component.state.currentMean)){
      return 'Loading...'
    }

    return <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {component.forceUpdate.bind(component)}
                  deleteHandler={()=>fireEvent(meanRep, 'delete', [component.state.currentMean])}>

              {rememberButton(component)}
              <Button bsStyle="default" bsSize="xsmall" onClick={()=>fireEvent('week-rep', 'unschedule-mean', [component.state.currentMean.id])}>Unschedule</Button>
              {showAlerts(component.state.alerts)}

              <form>
                <FormGroup controlId="formBasicText">

                  <div style={{display:'inline-block', paddingRight:'3px'}}>
                    <ControlLabel>Title:</ControlLabel>
                  </div>

                  <div style={{display:'inline-block'}}>

                    {component.state.mode.isEdit? <FormControl
                                      type="text"
                                      value={component.state.currentMean.title}
                                      placeholder="Enter title"
                                      onChange={component.handleNameVal}/>
                                    : <FormControl.Static>{component.state.currentMean.title}</FormControl.Static>}
                  </div>
                </FormGroup>
              </form>

              <LayersGroup mean={component.state.currentMean} isEdit = {component.state.mode.isEdit} />

          </CommonCrudeTemplate>
}

const prepareMean = function(mean){
  if(mean.id==DataConstants.newId){
    return true
  }

  if(mean.isFull == null || !mean.isFull){
    fireEvent(meanRep, 'get-full', [mean])
    return false
  }
  return true
}

const rememberButton = function(component){
  if(chkSt(meanRep, 'draggableMean')!=component.state.currentMean){
    return <Button bsStyle="default" bsSize="xsmall" onClick={()=>rememberReleaseHandler(component, 'remember')}>Remember</Button>
  } else {
    return <Button bsStyle="default" bsSize="xsmall" onClick={()=>rememberReleaseHandler(component, 'release')}>Release</Button>
  }
}

const rememberReleaseHandler = function(component, type){
  if(type == 'remember')
    fireEvent(meanRep, 'add-draggable', [component.state.currentMean])
  if(type == 'release')
    fireEvent(meanRep, 'remove-draggable')
}

const showAlerts = function(alerts){
  if(alerts!=null && alerts.length>0){
    const result = []
    for(var i in alerts){
      result.push(<li>{alerts[i]}</li>)
    }
    return <Alert bsStyle="danger">{result}</Alert>
  }
}
