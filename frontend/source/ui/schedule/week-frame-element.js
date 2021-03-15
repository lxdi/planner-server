import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {currentDateString} from '../../utils/date-utils'

const weekStyle = {marginBottom:'3px'}
const weekCurrentStyle = {marginBottom:'3px', border: '2px solid lightblue', borderRadius:'10px'}

const dayCellStyle = {border: '1px solid lightgrey', padding: '2px', borderRadius:'5px'}
const todayCellStyle = {border: '2px solid LightSalmon', padding: '2px', borderRadius:'5px'}

const greenDayStyle = {backgroundColor: 'HoneyDew'}
const yellowDayStyle = {backgroundColor: 'lemonchiffon'}
const redDayStyle = {backgroundColor: 'peachpuff'}

const urgencyUpcomingStyle = {border: '2px solid lightgrey'}
const urgencyAboutStyle = {border: '2px solid palegreen'}
const urgency1WeekLateStyle = {border: '2px solid lemonchiffon'}
const urgency2WeeksLateStyle = {border: '2px solid peachpuff'}

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
    // if(this.state.full){
    //   weekStyleVar = isCurrentWeek(this.props.week)?weekCurrentStyle: weekStyle
    // }

    return <div key = {this.props.week.id} style={weekStyleVar}>
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
  const style = JSON.parse(JSON.stringify(getFillmentStyle(day)))

  if(dayCal == '01'){
    Object.assign(style, {fontWeight: 'bold'})
  }

  return <a key = {day.id} href='#' onClick = {()=>fireEvent('day-modal', 'open', [day])}>
      <div style = {style}
      draggable='true'
      onDragStart={e => onDragStart(e, day)}
      onDragOver={e => e.preventDefault()}
      onDrop={e => onDrop(e, day)}>

        <div style = {getUrgencyStyle(day)}>
          <div style = {{verticalAlign: 'top', fontSize:'9px', display:'inline-block'}}>
            <div style = {{color: 'grey'}}>{dayCal}</div>
            <div style = {{color: 'red'}}>{dayCal=='01'? month: null}</div>
          </div>
          <div style = {{display:'inline-block', marginLeft: '3px'}}>{getTotalMappersAndRepsUI(day)}</div>
        </div>
      </div>
  </a>
}

const getUrgencyStyle = function(day){
  if(day.urgency == 'upcoming'){
    return urgencyUpcomingStyle
  }
  if(day.urgency == 'about week'){
    return urgencyAboutStyle
  }
  if(day.urgency == '1 week late'){
    return urgency1WeekLateStyle
  }
  if(day.urgency == '2 weeks late'){
    return urgency2WeeksLateStyle
  }
  return {}
}

const getFillmentStyle = function(day){
  const hours = day.mappersNumUnfinished*2 + day.repsNumUnfinished
  if(hours > 4){
    return redDayStyle
  }
  if(hours > 2){
    return yellowDayStyle
  }
  if(hours != 0) {
    return greenDayStyle
  }
  return {}
}

const getTotalMappersAndRepsUI = function(day){

  if(day.mappersNum == 0 && day.mappersNumUnfinished == 0 && day.repsNum == 0 && day.repsNumUnfinished == 0 && day.externalTasksNum == 0){
    return null
  }

  return <div>
            {zeroToDash(day.mappersNum + day.mappersNumUnfinished)}|{zeroToDash(day.repsNum + day.repsNumUnfinished)}|{zeroToDash(day.externalTasksNum)}
        </div>
}

const zeroToDash = function(num){
  if(num==0){
    return '-'
  }
  return num
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

const onDragStart = function(e, dayFrom){
  e.draggableDay = dayFrom
  e.eventType = 'moving'
}

const onDrop = function(e, dayTo){
  if(e.eventType == 'moving'){
    fireEvent('shift-plans-modal', 'open', [e.draggableDay, dayTo])
  }
  if(e.eventType == 'assign mean'){
    fireEvent('assign-mean-modal', 'open', [dayTo, e.mean])
    e.mean = null
  }
}
