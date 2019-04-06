import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from 'absevent'
import {WeekSchedule} from './week-schedule'

import {findSlotInPosition} from '../../utils/hquarters-utils'

const dumbHquarter = {dumb:true}

const createState = function(isOpen, isStatic, isEdit, hquarter){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    hquarter: hquarter
  }
}

const defaultState = function(){
  return createState(false, false, false, dumbHquarter)
}

export class HquarterModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState()
    this.okButton = this.okButton.bind(this)

    registerEvent('hquarter-modal', 'open', (stateSetter, hquarter)=>{
      const mode = hquarter.startWeek==null?{isStatic:true, isEdit:true}:this.state.mode
      this.setState({isOpen:true, hquarter:hquarter, mode: mode})
    })
    registerEvent('hquarter-modal', 'close', (stateSetter)=>{
      this.state.hquarter.isFull = false
      this.setState(defaultState())
    })
    registerReaction('hquarter-modal', 'hquarters-dao', ['add-slot', 'assign-slot', 'got-full', 'default-received'], (stateSetter)=>this.setState({}))
  }

  okButton(){
    if(viewStateVal('hquarters-dao', 'default')==this.state.hquarter){
      //fireEvent('hquarters-dao', 'hquarters-clean')
      fireEvent('hquarters-dao', 'update-default')
    } else {
      fireEvent('hquarters-dao', 'update', [this.state.hquarter])
    }
    fireEvent('hquarter-modal', 'close')
  }

  render(){
    return <CommonModal
              isOpen = {this.state.isOpen}
              okHandler = {this.okButton}
              cancelHandler = {()=>fireEvent('hquarter-modal', 'close')}
              title={this.state.hquarter.startWeek!=null? this.state.hquarter.startWeek.startDay + ' to ' + this.state.hquarter.endWeek.startDay: 'Default settings'}
              styleClass='hquarter-modal-style'>
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)}>
          {this.state.hquarter.dumb==null? modalBody(this):null}
        </CommonCrudeTemplate>
      </CommonModal>
  }
}

const modalBody = function(component){
  const hquarter = component.state.hquarter
  const isDefault = hquarter.startWeek==null
  if(hquarter.isFull!=null && hquarter.isFull){
        return     <div>
                    <div style={{display:'inline-block', padding:'10px', width:'8%', verticalAlign:'top'}}>
                      {isDefault || component.state.mode.isEdit?
                        <a href='#' onClick={()=>fireEvent('hquarters-dao', 'add-slot', [hquarter])}>+ Add slot</a>:null}
                      {getSlotsUI(component, hquarter)}
                    </div>
                    <div style={{display:'inline-block', padding:'10px', width:'92%', borderLeft:'1px solid lightgrey'}}>
                      <div>
                        {weekMappingTable(hquarter)}
                      </div>
                      <div style={{borderTop: '1px solid lightgrey', marginTop: '5px', paddingTop:'5px'}}>
                        <WeekSchedule hquarter = {hquarter}/>
                      </div>
                    </div>
                  </div>
  } else {
    if(isDefault){
      fireEvent('hquarters-dao', 'request-for-default')
    } else {
      fireEvent('hquarters-dao', 'get-full', [hquarter.id])
    }
    return 'Loading'
  }
}

const getSlotsUI = function(component, hquarter){
  const result = []
  for(var slotpos in hquarter.slots){
        const slot = hquarter.slots[slotpos]
        result.push(<div
                    draggable='true'
                    onDragStart={()=>fireEvent('hquarters-dao', 'add-draggable', [hquarter, slot])}
                    onDragEnd={()=>fireEvent('hquarters-dao', 'remove-draggable')}
                    style={component.state.mode.isEdit?{border: '1px dotted lightgrey', padding:'3px', borderRadius:'10px'}:null}>
                      Slot {slot.position}{getAssignedMean(slot)}
                    </div>)
  }
  return result
}

const getAssignedMean = function(slot){
  if(slot.meanid!=null){
    const mean = getFromMappedRepByid(viewStateVal('means-dao', 'means'), slot.meanid)
    return ' - ' + mean.title
  }
}

export const getFromMappedRepByid = function(rep, sid){
  for(var superid in rep){
    for(var id in rep[superid]){
      if(rep[superid][id].id === sid){
        return rep[superid][id]
      }
    }
  }
  return null
}

const week = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']
const weekFullName = {'mon': 'Monday', 'tue': 'Tuesday', 'wed': 'Wednsday', 'thu':'Thursday', 'fri': 'Friday', 'sat': 'Saturday', 'sun':'Sunday'}
const tasksSlots = [1,2,3]

const weekMappingTable = function(hquarter){
  const days = []
  const daystyle = {display: 'table-cell', width:'80px', textAlign:'right', paddingRight:'5px'}
  for(var i in week){
    const dayUI = []
    const day = week[i]
    dayUI.push(<div key={day} style={daystyle}>{day}</div>)
    for(var j in tasksSlots){
      const taskSlot = tasksSlots[j]
      const pos = findSlotInPosition(hquarter, day, taskSlot)
      dayUI.push(<div key={day + '_'+taskSlot} style={{display: 'table-cell', width: '40px', border: '1px solid lightgrey'}}
                  onDrop={()=>fireEvent('hquarters-dao', 'assign-slot', [day, taskSlot])}
                  onDragOver={(e)=>e.preventDefault()}
                  draggable='true'
                  onDragStart={()=>{if(pos!=null)fireEvent('hquarters-dao', 'add-draggable', [hquarter, pos.slot, pos.slotPosition])}}
                  onDragEnd={()=>fireEvent('hquarters-dao', 'remove-draggable')}>
                    {pos!=null?pos.slot.position:null}
                  </div>)
    }
    days.push(<div>{dayUI}</div>)
  }
  return days
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
