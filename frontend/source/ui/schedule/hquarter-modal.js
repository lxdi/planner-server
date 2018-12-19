import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

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
  return createState(false, true, false, dumbHquarter)
}

export class HquarterModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState()
    this.okButton = this.okButton.bind(this)

    registerEvent('hquarter-modal', 'open', (stateSetter, hquarter)=>this.setState({isOpen:true, hquarter:hquarter}))
    registerEvent('hquarter-modal', 'close', (stateSetter)=>this.setState(defaultState()))
    registerReaction('hquarter-modal', 'hquarters-dao', ['add-slot', 'assign-slot', 'got-full'], (stateSetter)=>this.setState({}))
  }

  okButton(){
    if(viewStateVal('hquarters-dao', 'default')==this.state.hquarter){
      fireEvent('hquarters-dao', 'hquarters-clean')
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
              styleClass='mean-modal-style'>
        <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)}>
          {this.state.hquarter.dumb==null? modalBody(this):null}
        </CommonCrudeTemplate>
      </CommonModal>
  }
}

const modalBody = function(component){
  return     <div>
              <div style={{display:'table-cell', borderRight:'1px solid lightgrey', padding:'10px'}}>
                {component.state.hquarter.isfull!=null || component.state.hquarter.startWeek==null?<a href='#' onClick={()=>fireEvent('hquarters-dao', 'add-slot', [component.state.hquarter])}>+ Add slot</a>:null}
                {getSlotsUI(component.state.hquarter)}
              </div>
              <div style={{display:'table-cell', padding:'10px'}}>
                <div>
                  {weekMappingTable(component.state.hquarter)}
                </div>
                <div style={{borderTop: '1px solid lightgrey', marginTop: '5px', paddingTop:'5px'}}>
                  {weeksWithTasksUI(component.state.hquarter)}
                </div>
              </div>
            </div>
}

const getSlotsUI = function(hquarter){
  const result = []
  if(hquarter.slots!=null){
    for(var slotpos in hquarter.slots){
        const slot = hquarter.slots[slotpos]
        result.push(<div
                    draggable='true'
                    onDragStart={()=>fireEvent('hquarters-dao', 'add-draggable', [hquarter, slot])}
                    onDragEnd={()=>fireEvent('hquarters-dao', 'remove-draggable')}>
                      Slot {slot.position}
                    </div>)
    }
  } else {
    fireEvent('hquarters-dao', 'get-full', [hquarter.id])
  }
  return result
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

const weeksWithTasksUI = function(hquarter){
  const result = []
  if(hquarter.weeks!=null && hquarter.weeks.length>0){
    for(var i in hquarter.weeks){
      const weekUI = []
      weekUI.push(<td>{hquarter.weeks[i].startDay}</td>)
      for(var dayOfWeekidx in week){
        const weekDayUI = []
        weekDayUI.push(<div>{weekFullName[week[dayOfWeekidx]]}</div>)
        if(hquarter.weeks[i].days!=null && hquarter.weeks[i].days[week[dayOfWeekidx]]!=null){
          for(var taskidx in hquarter.weeks[i].days[week[dayOfWeekidx]]){
            weekDayUI.push(<div>{hquarter.weeks[i].days[week[dayOfWeekidx]][taskidx].title}</div>)
          }
        }
        weekUI.push(<td style={{padding:'3px', border:'1px solid lightgrey'}}>{weekDayUI}</td>)
      }
      result.push(<table style={{borderCollapse:'collapse', border:'1px solid lightgrey'}}><tr>{weekUI}</tr></table>)
    }
  }
  return result
}


// const slotInCellUI = function(hquarter, day, position){
//   const pos =
//   if(pos!=null){
//     return pos.slot.position
//   }
// }

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
