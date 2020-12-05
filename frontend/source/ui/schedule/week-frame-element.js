import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {currentDateString} from '../../utils/date-utils'

const weekStyle = {marginBottom:'3px'}
const weekCurrentStyle = {marginBottom:'3px', border: '1px solid blue'}

const dayCellStyle = {border: '1px solid lightgrey', padding: '2px', borderRadius:'5px'}
const todayCellStyle = {border: '1px solid grey', padding: '2px', borderRadius:'5px'}

const greenDayStyle = {backgroundColor: 'palegreen'}
const yellowDayStyle = {backgroundColor: 'lemonchiffon'}
const redDayStyle = {backgroundColor: 'peachpuff'}

// props: week
export class WeekElement extends React.Component {
  constructor(props){
    super(props)
  }

  render(){

    if(this.props.week==null){
      return 'Loading...'
    }

    const days = []
    this.props.week.days.forEach(day => {
      days.push(<td>{getDayCellUI(day)}</td>)
    })

    return <div style={isCurrentWeek(this.props.week)?weekCurrentStyle: weekStyle}>
            {yearLabel(this.props.week)}
            <table style={{borderCollapse:'collapse', width:'100%'}}>
                  <tr>
                    {days}
                  </tr>
            </table>
          </div>
  }

}

const yearLabel = function(week){
  if(week.num!=1){
    return null
  }

  return <div style = {{borderBottom: '1px solid grey'}}> {week.year}</div>
}


const getDayCellUI = function(day){
  var style = null

  if(isCurrentDay(day)){
    style = todayCellStyle
  } else {
    style = dayCellStyle
  }

  return <div style={style}>
          <div style = {getFillmentStyle(day)}>
            {day.weekDay}: {day.mappersNum}/{day.repsNum}
          </div>
        </div>
}

const getFillmentStyle = function(day){
  if(day.mappersNum*2 + day.repsNum >= 4){
    return redDayStyle
  }
  if(day.mappersNum*2 != 0 && day.repsNum != 0){
    return yellowDayStyle
  }
  if(day.mappersNum*2 != 0 || day.repsNum != 0) {
    return greenDayStyle
  }
  return null
}

const isCurrentWeek = function(week){
  var result = false
  week.days.filter(day => isCurrentDay(day)).forEach(day => result = true)
  return result
}

const isCurrentDay = function(day){
  return currentDateString('-') == day.date
}
