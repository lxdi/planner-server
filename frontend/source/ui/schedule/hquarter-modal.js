import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

const dumbHquarter = {}

const createState = function(isOpen, isStatic, isEdit, hquarter){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    hquarter: hquarter
  }
}

const defaultState = function(){
  return createState(false, true, false, dumbHquarter)
}

export class HquarterModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState()

    registerEvent('hquarter-modal', 'open', (stateSetter, hquarter)=>this.setState({isOpen:true, hquarter:hquarter}))
    registerEvent('hquarter-modal', 'close', (stateSetter)=>this.setState(defaultState()))
    registerReaction('hquarter-modal', 'hquarters-dao', 'add-slot', (stateSetter)=>this.setState({}))
  }

  render(){
    return <CommonModal
              isOpen = {this.state.isOpen}
              okHandler = {()=>{fireEvent('hquarters-dao', 'update', [this.state.hquarter]);fireEvent('hquarter-modal', 'close')}}
              cancelHandler = {()=>fireEvent('hquarter-modal', 'close')}
              title={this.state.hquarter.year +'.'+formatDateNumber(this.state.hquarter.startday) + '.' + formatDateNumber(this.state.hquarter.startmonth)}
              styleClass='mean-modal-style'>
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)}>
            <div>
              <a href='#' onClick={()=>fireEvent('hquarters-dao', 'add-slot', [this.state.hquarter])}>+ Add slot</a>
              {getSlotsUI(this.state.hquarter)}
            </div>
        </CommonCrudeTemplate>
      </CommonModal>
  }
}

const getSlotsUI = function(hquarter){
  const result = []
  for(var slotpos in hquarter.slots){
      result.push(<div>Slot {hquarter.slots[slotpos].position}</div>)
  }
  return result
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
