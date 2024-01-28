import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {currentDateString, nextWeekDateString, prevWeekDateString} from '../../utils/date-utils'

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

    var weekType = detectWeek(this.props.week.days)

    if (weekType != null) {
      registerReaction(weekType+'week-frame', 'day-rep', ['got-days-in-week'], ()=>this.setState({}))
    }

  }

  render(){
    if(this.props.week==null){
      return 'Loading...'
    }

    var currentDay = getCurrentDay(this.props.week.days)
    var isFullDaysView = detectWeek(this.props.week.days) != null
    var fullDays = null

    if (isFullDaysView) {
      var fullDaysByWeek = chkSt('day-rep', 'full-days-by-week')

      if (fullDaysByWeek == null || fullDaysByWeek[this.props.week.id] == null) {
        fireEvent('day-rep', 'get-days-in-week', [this.props.week.id])
      } else {
        fullDays = fullDaysByWeek[this.props.week.id]
      }
    }

    const daysUIcells = []

    this.props.week.days.forEach(day => {
      var dayFull = fullDays!=null? fullDays[day.id]: null
      daysUIcells.push(<td>{getDayCellUI(day, currentDay==day, isFullDaysView, dayFull)}</td>)
    })

    var weekStyleVar = weekStyle //currentDay==null? weekStyle: weekCurrentStyle

    return <div key = {this.props.week.id} style={weekStyleVar}>
            {yearLabel(this.props.week)}
            <table style={{borderCollapse:'collapse', width:'100%', tableLayout: 'fixed'}}>
                  <tr>
                    {daysUIcells}
                  </tr>
            </table>
          </div>
  }
}

const detectWeek = function(days) {
  if (getCurrentDay(days) != null) {
    return 'current'
  }

  if (getNextWeekDay(days) != null) {
    return 'next'
  }

  if (getPrevWeekDay(days) != null) {
    return 'prev'
  }

  return null
}

const yearLabel = function(week){
  if(week.num!=1){
    return null
  }
  return <div style = {{borderBottom: '1px solid grey'}}> {week.year}</div>
}

const getDayCellUI = function(day, isCurrent, isCurrentWeek, dayFull){
  var style = isCurrent? todayCellStyle: dayCellStyle
  var isFull = day.id != null

  return <div style={style}>
          {isFull? getDayContentFull(day, isCurrentWeek, dayFull): <div style={{fontWeight: 'bold'}}>{day.weekDay}</div>}
        </div>
}

const getDayContentFull = function(day, isCurrentWeek, dayFull){

  const dayCal = formatDate(day.date, 'day')
  const month = formatDate(day.date, 'month')
  const style = JSON.parse(JSON.stringify(getFillmentStyle(day)))

  if(dayCal == '01'){
    Object.assign(style, {fontWeight: 'bold'})
  }

  var taskContent = null
  var dayNumStyle = null

  if (isCurrentWeek) {
    taskContent = <div style = {{marginLeft: '3px'}}>{getFullContentUi(dayFull)}</div>
    dayNumStyle = {verticalAlign: 'top', fontSize:'9px'}
  } else {
    taskContent = <div style = {{display:'inline-block', marginLeft: '3px'}}>{getTotalMappersAndRepsUI(day)}</div>
    dayNumStyle = {verticalAlign: 'top', fontSize:'9px', display:'inline-block'}
  }

  return <a key = {day.id} href='#' onClick = {()=>fireEvent('day-modal', 'open', [day])}>
      <div style = {style}
          draggable='true'
          onDragStart={e => onDragStart(e, day)}
          onDragOver={e => e.preventDefault()}
          onDrop={e => onDrop(e, day)}>

        <div style = {isCurrentWeek? Object.assign({height: '120px'}, getUrgencyStyle(day)): getUrgencyStyle(day)}>
          <div style = {dayNumStyle}>
            <div style = {{color: 'grey'}}>{dayCal}</div>
            <div style = {{color: 'red'}}>{dayCal=='01'? month: null}</div>
          </div>
          {taskContent}
        </div>
      </div>
  </a>
}

const getFullContentUi = function(fullDayDto) {

  if (fullDayDto == null) {
    return 'Loading...'
  }

  var tasksUI = fullDayDto.taskMappers.map(tm => getFullContentElementUI(tm.taskFullPath))
  var repsUi = fullDayDto.repetitions.map(rp => getFullContentElementUI(rp.taskFullPath))
  var externalTasksUI = fullDayDto.externalTasks.map(et => getFullContentElementUI(et.description))

  return <div style={{fontSize: '7pt'}}>
            <div style = {{color: 'green'}}>{tasksUI}</div>
            <div style = {{color: 'DodgerBlue'}}>{repsUi}</div>
            <div style = {{color: 'brown'}}>{externalTasksUI}</div>
            <div style = {{color: 'DarkSlateGrey'}}>{formatSlotActivity(fullDayDto.slotActivity)}</div>
  </div>

}

const getFullContentElementUI = function(name) {
  var maxLength = 25
  var result = name.includes('/')? name.split('/').pop(): name
  result = result.length > maxLength? result.substring(0, maxLength-3)+'...': result
  return <div>- {result}</div>
}

const formatSlotActivity = function(value) {
  if (value == null || value == '') {
    return null
  }

  var splitted = value.split(';')
  return splitted.map(sv => <div>{getFullContentElementUI(sv)}</div>)
}

const divisor = function(){
  return <div style={{backgroundColor:'lightgrey', width:'100%', height:'1px', marginLeft:'2px', marginRight:'2px', marginTop:'1px', marginBottom:'1px'}}></div>
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

const getCurrentDay = function(days) {
  return findDay(days, currentDateString('-'))
}

const getNextWeekDay = function(days) {
  return findDay(days, nextWeekDateString('-'))
}

const getPrevWeekDay = function(days) {
  return findDay(days, prevWeekDateString('-'))
}

const findDay = function(days, targetDay) {
  for (var i in days) {
    if (days[i].date == targetDay) {
      return days[i]
    }
  }
  return null
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
  fireEvent('drag-n-drop', 'put', ['moving', dayFrom])
}

const onDrop = function(e, dayTo){
  const type = chkSt('drag-n-drop','type')

  if(type == 'moving'){
    const dayFrom = chkSt('drag-n-drop','data')
    fireEvent('shift-plans-modal', 'open', [dayFrom, dayTo])
  }

  if(type == 'assign-mean'){
    const mean = chkSt('drag-n-drop','data')
    fireEvent('assign-mean-modal', 'open', [dayTo, mean])
    e.mean = null
  }

  fireEvent('drag-n-drop', 'clean')
}
