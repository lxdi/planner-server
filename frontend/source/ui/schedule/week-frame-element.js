import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {currentDateString} from '../../utils/date-utils'

const weekStyle = {marginBottom:'3px'}
const weekCurrentStyle = {marginBottom:'3px', border: '2px solid lightblue', borderRadius:'10px'}

const dayCellStyle = {border: '1px solid lightgrey', padding: '2px', borderRadius:'5px'}
const todayCellStyle = {border: '2px solid LightSalmon', padding: '2px', borderRadius:'5px'}

const greenDayStyle = {backgroundColor: 'palegreen'}
const yellowDayStyle = {backgroundColor: 'lemonchiffon'}
const redDayStyle = {backgroundColor: 'peachpuff'}

// props: week, full
export class WeekElement extends React.Component {
  constructor(props){
    super(props)
    this.state = {full: this.props.full}
    if(this.state.full==null){
      this.state.full = true
    }
  }

  render(){
    if(this.props.week==null){
      return 'Loading...'
    }

    const days = []
    this.props.week.days.forEach(day => {
      days.push(<td>{getDayCellUI(day, this.state.full)}</td>)
    })

    var weekStyleVar = weekStyle
    if(this.state.full){
      weekStyleVar = isCurrentWeek(this.props.week)?weekCurrentStyle: weekStyle
    }

    return <div style={weekStyleVar}>
            {yearLabel(this.props.week)}
            <table style={{borderCollapse:'collapse', width:'100%', tableLayout: 'fixed'}}>
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


const getDayCellUI = function(day, isFull){
  var style = null

  if(isCurrentDay(day)){
    style = todayCellStyle
  } else {
    style = dayCellStyle
  }

  return <div style={style}>
          {isFull? getDayContentFull(day): <div style={{fontWeight: 'bold'}}>{day.weekDay}</div>}
        </div>
}

const getDayContentFull = function(day){
  const dayCal = formatDate(day.date, 'day')
  const month = formatDate(day.date, 'month')
  const style = getFillmentStyle(day)

  if(dayCal == '01'){
    Object.assign(style, {fontWeight: 'bold'})
  }

  return <a href='#' onClick = {()=>fireEvent('day-modal', 'open', [day])}>
      <div style = {style}>
        <div style = {{verticalAlign: 'top', fontSize:'9px', display:'inline-block'}}>
          <div style = {{color: 'grey'}}>{dayCal}</div>
          <div style = {{color: 'red'}}>{dayCal=='01'? month: null}</div>
        </div>
        <div style = {{display:'inline-block', marginLeft: '3px'}}> {day.mappersNum}/{day.repsNum}</div>
      </div>
  </a>
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
  return {}
}

const isCurrentWeek = function(week){
  var result = false
  week.days.filter(day => isCurrentDay(day)).forEach(day => result = true)
  return result
}

const isCurrentDay = function(day){
  return currentDateString('-') == day.date
}

const formatDate = function(date, part){
  const splited = date.split('-')
  if(part == 'day'){
    return splited[2]
  }
  if(part == 'month'){
    return splited[1]
  }
  return ''
}
