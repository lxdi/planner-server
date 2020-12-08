import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'


export class DayModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, day: null}
    this.state = defaultState

    registerEvent('day-modal', 'open', (stateSetter, day)=>this.setState({isOpen:true, day:day}))
    registerEvent('day-modal', 'close', (stateSetter)=>this.setState(defaultState))

    registerReaction('day-modal', DataConstants.dayRep, ['got-one'], ()=>this.setState({}))
  }

  render(){
    return <CommonModal
                    isOpen = {this.state.isOpen}
                    cancelHandler = {()=>fireEvent('day-modal', 'close')}
                    title='Day'>
                    {getContent(this)}
              </CommonModal>
  }
}

const getContent = function(comp){
  if(comp.state.day == null){
    return null
  }

  if(chkSt(DataConstants.dayRep, DataConstants.objMap) == null || chkSt(DataConstants.dayRep, DataConstants.objMap)[comp.state.day.id]==null
      || !comp.state.day.isFull){

    fireEvent(DataConstants.dayRep, 'get-one', [comp.state.day])
    return 'Loading...'
  }

  return 'TODO'
}
