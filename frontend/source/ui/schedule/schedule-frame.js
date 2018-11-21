import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {QuartersState} from '../../data/quarters-dao'
import {registerEvent, registerReaction, fireEvent} from '../../controllers/eventor'

export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {};
    registerReaction('schedule-frame', 'quarters-dao', 'quarters-received', ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        <div>

        </div>
        <div>
        <Table striped bordered condensed hover>
          <tbody>
            {quartersUI()}
          </tbody>
        </Table>
        </div>
      </div>
    )
  }
}

const quartersUI = function(){
  if(QuartersState.quarters != null){
    return QuartersState.quarters.map((quarter)=>
        <tr>
          <td style = {{width:'100px'}}>
            {quarter.year +'.'+formatDateNumber(quarter.startDay) + '.' + formatDateNumber(quarter.startMonth)}
          </td>
        </tr>
      )
  } else {
    fireEvent('quarters-dao', 'quarters-request', [])
  }
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}

const isCurrentWeek = function(week){
  if(week.startMonth == CurrentDate.month && week.startDay <= CurrentDate.day){
    if(CurrentDate.month != week.endMonth){
      return true
    } else {
      if(CurrentDate.day <= week.endDay){
        return true
      }
    }
  }
  return false
}
