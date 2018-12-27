import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {HquarterModal} from './hquarter-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {edit:false};
    registerReaction('schedule-frame', 'hquarters-dao', ['hquarters-received', 'hquarter-modified', 'hquarters-clean', 'default-received'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'means-dao', ['means-received', 'mean-modified'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))

    registerEvent('schedule-frame', 'switch-edit-mode', (stateSetter)=>this.setState({edit:!this.state.edit}))
  }

  allowDrop(ev) {
    ev.preventDefault();
}

  render(){
    return(
      <div>
        <HquarterModal/>
        <ButtonGroup>
          <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('hquarter-modal', 'open', [viewStateVal('hquarters-dao', 'default')])}>Default settings</Button>
          <Button bsStyle="default" bsSize="xsmall" onClick={()=>fireEvent('schedule-frame', 'switch-edit-mode')}>{this.state.edit?'View': 'Edit'}</Button>
        </ButtonGroup>
        <div>
          {viewStateVal('means-dao', 'means')!=null?hquartersUI(this):null}
        </div>
      </div>
    )
  }
}

const hquartersUI = function(component){
  if(viewStateVal('hquarters-dao', 'hquarters') != null && viewStateVal('hquarters-dao', 'default')!=null){
    return viewStateVal('hquarters-dao', 'hquarters').map((hquarter)=>
        <div>
          <Table striped bordered condensed hover width={'100px'} key={"hquarter_"+weekToString(hquarter.startWeek)} >
            <tbody>
              <tr>
                <td>
                  <a href='#' onClick={()=>fireEvent('hquarter-modal', 'open', [hquarter])} style={isCurrentHquarter(hquarter)?{fontWeight: 'bold'}:{}}>
                    {weekToString(hquarter.startWeek)}
                  </a>
                </td>
              </tr>
              {getSlotsUI(component, hquarter)}
            </tbody>
          </Table>
          </div>
      )
  } else {
    if(viewStateVal('hquarters-dao', 'default')==null){
      fireEvent('hquarters-dao', 'request-for-default')
    } else {
      if(viewStateVal('hquarters-dao', 'hquarters')==null){
        fireEvent('hquarters-dao', 'hquarters-request')
      }
    }
    return 'Loading...'
  }
}

const weekToString = function(week){
  return week!=null? week.startDay: "default week (TODO - remove!)"
}

const getSlotsUI = function(component, hquarter){
  const result = []
  for(var slotpos in hquarter.slotsLazy){
    const slot = hquarter.slotsLazy[slotpos]
    result.push(getSlotView(component, slot))
  }
  return result
}

const getSlotView = function(component, slot){
  if(slot.meanid!=null){
    const mean = findMean(slot.meanid)
    const realm = viewStateVal('realms-dao', 'realms')[mean.realmid]
    return <tr>
                    <td>
                      <a href='#' style={getStyleFroSlot(slot)}>{getSlotTitleWithMean(slot.meanid)}</a>
                      {component.state.edit? <a href='#' onClick={()=>fireEvent('hquarters-dao', 'unassign-mean', [slot])}> X </a>:null}
                    </td>
                  </tr>
  } else {
    return <tr
                  onDragOver={(e)=>{e.preventDefault()}}
                  onDrop={(e)=>fireEvent('hquarters-dao', 'assign-mean-to-slot', [viewStateVal('means-dao', 'draggableMean'), slot])}>
                    <td>
                      <span style={{color:'lightgrey'}}>Slot {slot.position}</span>
                    </td>
                  </tr>
  }
}

const getStyleFroSlot = function(slot){
  if(slot.tasksInLayer == null){
    return {color: 'darkgrey'}
  }
  if(slot.tasksInLayer < 9){
    return {color: 'orange'}
  }
  if(slot.tasksInLayer == 9){
    return {color: 'LimeGreen'}
  }
  if(slot.tasksInLayer > 9){
    return {color: 'red'}
  }
}

const getSlotTitleWithMean = function(meanid){
  const mean = findMean(meanid)
  const realm = viewStateVal('realms-dao', 'realms')[mean.realmid]
  var chain = []
  var currMean = mean
  while(currMean!=null){
    chain.unshift(currMean.title)
    currMean = viewStateVal('means-dao', 'means')[realm.id][currMean.parentid]
  }
  var result = realm.title
  for(var i in chain){
    result = result + ' / ' + chain[i]
  }
  return result
}

const findMean = function(meanid){
  for(var realmid in viewStateVal('means-dao', 'means')){
    const means = viewStateVal('means-dao', 'means')[realmid]
    if(means[meanid]!=null){
      return means[meanid]
    }
  }
  return null
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}

const isCurrentHquarter = function(hquarter){
  const todayTime = Date.parse('2018-11-05')//new Date().getTime()
  const beginTime = Date.parse(hquarter.startWeek.startDay)
  const endTime = Date.parse(hquarter.endWeek.startDay)
  return todayTime>beginTime && todayTime<endTime
}
