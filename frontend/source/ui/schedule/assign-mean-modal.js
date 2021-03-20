import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'
import {isCheckedTask, checkTask, isLayerChecked, checkLayer} from '../../service/assign-mean-check-service'

export class AssignMeanModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, dayTo:null, mean: null, dto: null}
    this.state = defaultState

    registerEvent('assign-mean-modal', 'open', (stateSetter, dayTo, mean)=>this.setState({isOpen:true, dayTo:dayTo, mean: mean, dto:{startDayId: dayTo.id, tasksPerWeek: 1}}))
    registerEvent('assign-mean-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('assign-mean-modal', 'mean-rep', ['got-full'], ()=>this.setState({}))
    registerReaction('assign-mean-modal', 'week-rep', ['assign-mean-done'], ()=>this.setState(defaultState))
  }

  render(){
    return <CommonModal
                    isOpen = {this.state.isOpen}
                    okHandler={()=>okHandler(this)}
                    cancelHandler = {()=>fireEvent('assign-mean-modal', 'close')}
                    title={getTitle(this)}>
                    {getContent(this)}
              </CommonModal>
  }
}

const getTitle = function(comp){
  if(comp.state.mean == null || comp.state.dayTo == null){
    return ''
  }

  return 'Assign mean ' + comp.state.mean.title +' to '+comp.state.dayTo.date
}

const okHandler = function(comp){
  const dto = comp.state.dto
  fireEvent('week-rep', 'assign-mean', [dto])
}

const getContent = function(comp){

  if(comp.state.dayTo == null){
    return null
  }

  const mean = comp.state.mean

  if(mean.isFull == null || !mean.isFull){
    fireEvent('mean-rep', 'get-full', [mean])
    return 'Loading...'
  }

  return <div>
            {layersUI(comp, mean)}
          </div>
}

const layersUI = function(comp, mean){
  const result = []

  if(mean.layers!=null){
    mean.layers.forEach(layer => {
      result.push(
        <div>
          {checkBoxUI('Layer ' + layer.priority, isLayerChecked(comp.state.dto, layer), ()=>checkLayer(comp, comp.state.dto, layer))}
          <div style={{marginLeft:'10px'}}>{tasksUI(comp, layer)}</div>
        </div>)
    })
  }

  return <div>{result}</div>

}

const tasksUI = function(comp, layer){
  const result = []

  if(layer.tasks!=null){
    layer.tasks.forEach(t => {
      const task = t
      result.push(<div id={task.id}>
        {checkBoxUI(task.title, isCheckedTask(comp.state.dto, task), ()=>checkTask(comp, comp.state.dto, layer, task))}
        </div>)
    })
  }

  return <dev>{result}</dev>
}

const checkBoxUI = function(title, isChecked, checkCallback){
    return     <div id={title}>
                <div style={{display:'inline-block'}}>
                  <input type="checkbox" checked={isChecked? 'checked': null} onClick={(e)=>{e.target.checked = !checkCallback.call()}}/>
                </div>
                <div style={{display:'inline-block', marginLeft:'3px'}}>
                  {title}
                </div>
              </div>
}

const inputCheckbox = function(isChecked, checkCallback){
  if(isChecked){
    return <input type="checkbox" checked={'checked'} onClick={(e)=>{e.preventDefault(); checkCallback.call()}}/>
  } else {
    return <input type="checkbox" onClick={(e)=>{e.preventDefault(); checkCallback.call()}}/>
  }
}
