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

export class MeanModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, mode:{isStatic: true, isEdit: false}, currentMean: null, postactions: null}
    this.state = defaultState
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);

    registerEvent('mean-modal', 'open', (stateSetter, currentMean) => {
      this.setState({isOpen:true, mode:{isStatic: currentMean.id==DataConstants.newId, isEdit: currentMean.id==DataConstants.newId}, currentMean: currentMean})
    })

    registerEvent('mean-modal', 'open-with-task', (stateSetter, currentMean, taskId, highlightId) => {
      this.setState({
        isOpen:true,
        mode:{isStatic: currentMean.id==DataConstants.newId, isEdit: currentMean.id==DataConstants.newId},
        currentMean: currentMean,
        postactions: {open:{task:{id: taskId, highlightId: highlightId}}}
      })
    })

    registerEvent('mean-modal', 'close', ()=>{
      this.state.currentMean.isFull = false
      fireEvent(layerRep, 'clean')
      fireEvent(taskRep, 'clean')
      fireEvent(DataConstants.topicRep, 'clean')
      fireEvent(DataConstants.tasktestingRep, 'clean')
      this.setState(defaultState)
    })

    registerEvent('mean-modal', 'remove-target')
    registerReaction('means-modal', meanRep, ['deleted', 'updated', 'created'], ()=>this.setState(defaultState))
    registerReaction('means-modal', meanRep, ['got-full'], ()=>{postactionsHandle(this.state); this.setState({})})
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
    if(component.state.currentMean==null){
      return null
    }

    if(!prepareMean(component.state.currentMean)){
      return 'Loading...'
    }

    return <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {component.forceUpdate.bind(component)}
                  deleteHandler={()=>fireEvent(meanRep, 'delete', [component.state.currentMean])}>

              {unscheduleButton(component)}
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

const unscheduleButton = function(comp){
  const action = ()=>fireEvent('week-rep', 'unschedule-mean', [comp.state.currentMean.id])
  const message = 'Are sure you want to unschedule this Mean?'
  return <Button bsStyle="default" bsSize="xsmall" onClick={()=> fireEvent('confirm-modal', 'open', [message, action])}>
          Unschedule
        </Button>
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

const postactionsHandle = function(state){
  if(state.postactions==null){
    return
  }
  if(state.postactions.open.task!=null){
    openTaskModal(state.currentMean, state.postactions.open.task)
  }
}

const openTaskModal = function(mean, taskArgs){
  mean.layers.forEach(layer => {
    layer.tasks.forEach(task => {
      if(task.id == taskArgs.id){
        fireEvent('task-modal', 'open-testings-hidden', [layer, task, taskArgs.highlightId])
      }
    })
  })
}
