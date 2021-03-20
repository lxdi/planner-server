import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common/common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'
import {isCheckedTask, checkTask, isLayerChecked, checkLayer} from '../../service/assign-mean-check-service'

export class AssignMeanModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, dayTo:null, mean: null, dto: null}
    this.state = defaultState

    registerEvent('assign-mean-modal', 'open', (stateSetter, dayTo, mean)=>this.setState({isOpen:true, dayTo:dayTo, mean: mean, dto:{startDayId: dayTo.id, tasksPerWeek: 0}}))
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

  const style = {border:'1px solid lightgrey', margin: '3px', padding: '3px', borderRadius: '10px'}
  return <div>
            <div id='choose per week' style={style}>
              <div id='1 per week'>{radioUI('1 per week', comp.state.dto.tasksPerWeek==1, ()=>{comp.state.dto.tasksPerWeek=1; comp.setState({}); return true})}</div>
              <div id='2 per week'>{radioUI('2 per week', comp.state.dto.tasksPerWeek==2, ()=>{comp.state.dto.tasksPerWeek=2; comp.setState({}); return true})}</div>
              <div id='3 per week'>{radioUI('3 per week', comp.state.dto.tasksPerWeek==3, ()=>{comp.state.dto.tasksPerWeek=3; comp.setState({}); return true})}</div>
            </div>
            <div style={style}>
              {layersUI(comp, mean)}
            </div>
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
  return inputCheckUI(title, isChecked, checkCallback, 'checkbox')
}

const radioUI = function(title, isChecked, checkCallback){
  return inputCheckUI(title, isChecked, checkCallback, 'radio')
}

const inputCheckUI = function(title, isChecked, checkCallback, type){
    return     <div id={title}>
                <div style={{display:'inline-block'}}>
                  <input type={type} checked={isChecked? 'checked': null} onClick={(e)=>{e.target.checked = checkCallback.call()}}/>
                </div>
                <div style={{display:'inline-block', marginLeft:'3px'}}>
                  {title}
                </div>
              </div>
}
