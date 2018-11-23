import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {QuartersState} from '../../data/quarters-dao'
import {MeansState} from '../../data/means-dao'
import {RealmsState} from '../../data/realms-dao'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'


export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {};
    registerReaction('schedule-frame', 'quarters-dao', 'quarters-received', ()=>this.setState({}))
    registerReaction('schedule-frame', 'means-dao', ['means-received', 'modify'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
  }

  allowDrop(ev) {
    ev.preventDefault();
}

  render(){
    return(
      <div>
        <div>
          {MeansState.meansLoaded?quartersUI():null}
        </div>
      </div>
    )
  }
}

const quartersUI = function(){
  if(QuartersState.quarters != null){
    return QuartersState.quarters.map((quarter)=>
    <Table striped bordered condensed hover width={'100px'}>
      <tbody>
        <tr>
          <td>
            {quarter.year +'.'+formatDateNumber(quarter.startDay) + '.' + formatDateNumber(quarter.startMonth)}
          </td>
        </tr>
        <tr onDragOver={(e)=>{e.preventDefault()}} onDrop={(e)=>fireEvent('means-dao', 'assign-quarter-to-draggable', [quarter, 1])}>
          <td>
            {getMeanSlotUI(quarter, 1)}
          </td>
        </tr>
        <tr onDragOver={(e)=>{e.preventDefault()}} onDrop={(e)=>fireEvent('means-dao', 'assign-quarter-to-draggable', [quarter, 2])}>
          <td>
            {getMeanSlotUI(quarter, 2)}
          </td>
        </tr>
      </tbody>
    </Table>
      )
  } else {
    fireEvent('quarters-dao', 'quarters-request', [])
  }
}

const getMeanSlotUI = function(quarter, position){
  const mean = getMean(quarter, position)
  if(mean==null){
    return 'Slot ' + position
  } else {
    return <div>
              <a href='#'>{mean.title}</a>
              <a href='#' onClick={()=>fireEvent('means-dao', 'unassign-quarter', [mean])}> X</a>
          </div>
  }
}

const getMean = function(quarter, position){
  for(var meanid in MeansState.means){
    const currentMean = MeansState.means[meanid]
    if(currentMean.realmid == RealmsState.currentRealm.id
        && currentMean.quarterid == quarter.id
        && currentMean.position == position){
        return currentMean
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
