import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {};
    registerReaction('schedule-frame', 'quarters-dao', 'quarters-received', ()=>this.setState({}))
    registerReaction('schedule-frame', 'means-dao', ['means-received', 'mean-modified'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
  }

  allowDrop(ev) {
    ev.preventDefault();
}

  render(){
    return(
      <div>
        <div>
          {viewStateVal('means-dao', 'means')!=null?quartersUI():null}
        </div>
      </div>
    )
  }
}

const quartersUI = function(){
  if(viewStateVal('quarters-dao', 'quarters') != null){
    return viewStateVal('quarters-dao', 'quarters').map((quarter)=>
          <Table striped bordered condensed hover width={'100px'} key={quarter.year +'.'+formatDateNumber(quarter.startDay) + '.' + formatDateNumber(quarter.startMonth)}>
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
    return <span style={{color: 'lightgrey'}}>slot {position}</span>
  } else {
    return <div>
              <a href='#'>{mean.title}</a>
              <a href='#' onClick={()=>fireEvent('means-dao', 'unassign-quarter', [mean])}> X</a>
          </div>
  }
}

const getMean = function(quarter, position){
  const means = viewStateVal('means-dao', 'means')
  for(var meanid in means){
    const mean = means[meanid]
    const currentRealm = viewStateVal('realms-dao', 'currentRealm')
    if(currentRealm!=null && mean.realmid == currentRealm.id
        && mean.quarterid == quarter.id
        && mean.position == position){
        return mean
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
