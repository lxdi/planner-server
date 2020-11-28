import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {iterateLLfull, iterateTree} from 'js-utils'

import {CreateMean, CreateTarget, CreateLayer} from './../../data/creators'
import {meanModalHeaderTitle, targetsDropDownTitle} from './../../titles'
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {isValidMean} from '../../utils/mean-validator'

const newId = 'new'
const realmRep = 'realm-rep'
const targetRep = 'target-rep'
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
  return createState(false, true, false, CreateMean(newId, '', null, []))
}

export class MeanModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('mean-modal', 'open', function(stateSetter, currentMean){
      this.setState(createState(true, currentMean.id==newId, currentMean.id==newId, currentMean))
      return currentMean
    }.bind(this))

    registerEvent('mean-modal', 'close', function(){
      this.setState(defaultState())
    }.bind(this))

    registerEvent('mean-modal', 'remove-target')

    registerReaction('means-modal', meanRep, ['deleted', 'updated', 'created'], function(){
      fireEvent('mean-modal', 'close')
      this.setState({})
    }.bind(this))

    registerReaction('mean-modal', meanRep, 'got-full', ()=>this.setState({}))
    registerReaction('mean-modal', layerRep, ['by-mean-response'], ()=>this.setState({}))
    registerReaction('mean-modal', taskRep, ['by-mean-response', 'move-task'], ()=>this.setState({}))
    registerReaction('mean-modal', 'task-modal', 'close', ()=>this.setState({}))

  }

  okHandler(){
    if(this.state.currentMean.id==newId){
      console.log(this.state.currentMean)
      fireEvent(meanRep, 'create', [this.state.currentMean])
    } else {
      this.state.currentMean.isFull=false
      fireEvent(meanRep, 'update', [this.state.currentMean])
    }
  }

  handleNameVal(e){
    this.state.currentMean.title = e.target.value;
    this.setState({});
  }

  render(){
    // if(!prepareMean(this.state.currentMean)){
    //   return 'Loading...'
    // }
    return renderUI(this)
  }
}


const renderUI = function(component){
  component.state.alerts = []
  return <CommonModal
            isOpen = {component.state.isOpen}
            okHandler = {isValidMean(component.state.alerts, component.state.currentMean)?component.okHandler:null}
            cancelHandler = {()=>fireEvent('mean-modal', 'close', [])}
            title={meanModalHeaderTitle}
            styleClass='mean-modal-style'>
            {modalBody(component)}
    </CommonModal>
}

const modalBody = function(component){
    if(!prepareMean(component.state.currentMean)){
      return 'Loading...'
    }
    return <CommonCrudeTemplate editing = {component.state.mode} changeEditHandler = {component.forceUpdate.bind(component)} deleteHandler={()=>fireEvent(meanRep, 'delete', [component.state.currentMean.id])}>
              {rememberButton(component)}
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
              {layersBlock(component, component.state.currentMean, component.state.mode.isEdit)}
          </CommonCrudeTemplate>
}

const prepareMean = function(mean){
  if(mean.layers!=null){
    return true
  }

  if(mean.id == newId){
    mean.layers = []
    return true
  }

  const layersByMean = chkSt(layerRep, indexByMean)
  if(layersByMean==null){
    fireEvent(layerRep, byMeanRequest, [mean.id])
    return false
  }

  const layers = chkSt(layerRep, indexByMean)[mean.id]

  if(layers==null){
    mean.layers = []
    return true
  }
  mean.layers = getEntriesAsList(layers)
  mean.layers.forEach(layer => layer.tasks = [])

  const allTasksByMean = chkSt(taskRep, indexByMean)
  if(allTasksByMean == null){
    fireEvent(taskRep, byMeanRequest, [mean.id])
    return false
  }

  const tasks = chkSt(taskRep, indexByMean)[mean.id]

  if(tasks == null){
    return true
  }

  for(const id in tasks){
    const layerid = tasks[id].layerid
    layers[layerid].tasks.push(tasks[id])
  }

  return true
}

const getEntriesAsList = function(obj){
  const result = []
  for(const id in obj){
    result.push(obj[id])
  }
  return result
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

const layersBlock = function(component, mean, isEdit){
    return <ListGroup>
              <div>
                <h4>Layers</h4>
                {isEdit? getCreateLayerButton(component, mean): null}
              </div>
              <ListGroup>
                {listLayersGroupContent(mean, isEdit)}
              </ListGroup>
            </ListGroup>
}

const listLayersGroupContent = function(mean, isEdit){
    const result = []

    for(var id in mean.layers){
      const layer = mean.layers[id]
      result.push(<ListGroupItem key={'layer_'+layer.priority}>
                              <div style={{fontWeight:'bold', fontSize:'12pt'}}>Layer {layer.priority}</div>
                              <div>TODO; layer priority: {layer.priority}</div>
                            </ListGroupItem>)
    }
    return result
}

const getCreateLayerButton = function(component, mean){
  return <Button bsStyle="primary" bsSize="xsmall"  onClick={()=>{addNewLayerToMean(mean); component.setState({})}}>
                              Create layer
                          </Button>
}

const addNewLayerToMean = function(mean){
  var priority = 0
  for(const id in mean.layers){
    if(mean.layers[id].priority>priority){
      priority = mean.layers[id].priority
    }
  }
  priority = priority + 1
  mean.layers.push(CreateLayer(newId, priority, mean.id))
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
