import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../common-modal'
import {formatDate} from '../../utils/date-utils'
import {DataConstants} from '../../data/data-constants'

export class AssignMeanModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, dayTo:null, mean: null}
    this.state = defaultState

    registerEvent('assign-mean-modal', 'open', (stateSetter, dayTo, mean)=>this.setState({isOpen:true, dayTo:dayTo, mean: mean}))
    registerEvent('assign-mean-modal', 'close', (stateSetter)=>this.setState(defaultState))

    // registerReaction('assign-mean-modal', DataConstants.dayRep, ['got-one'], ()=>this.setState({}))
    // registerReaction('assign-mean-modal', DataConstants.weekRep, ['moved-plans'], ()=>this.setState(defaultState))
  }

  render(){
    return <CommonModal
                    isOpen = {this.state.isOpen}
                    okHandler={()=>console.log("TODO")}
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

const getContent = function(comp){

  if(comp.state.dayTo == null){
    return null
  }

  // if(chkSt(DataConstants.dayRep, DataConstants.objMap) == null || chkSt(DataConstants.dayRep, DataConstants.objMap)[comp.state.dayFrom.id]==null
  //     || !comp.state.dayFrom.isFull){
  //
  //   fireEvent(DataConstants.dayRep, 'get-one', [comp.state.dayFrom])
  //   return 'Loading...'
  // }

  return <div>
            TODO
          </div>
}
