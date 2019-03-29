import React from 'react';
import ReactDOM from 'react-dom';
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

const week = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']
const weekFullName = {'mon': 'Monday', 'tue': 'Tuesday', 'wed': 'Wednsday', 'thu':'Thursday', 'fri': 'Friday', 'sat': 'Saturday', 'sun':'Sunday'}
const borderStyle = '1px solid lightgrey'
const dayInMilliseconds = 86400000

//props: hquarter
export class WeekSchedule extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    const result = []
    const hquarter = this.props.hquarter
    if(hquarter.weeks!=null && hquarter.weeks.length>0){
      for(var i in hquarter.weeks){
        result.push(getWeekUI(hquarter.weeks[i]))
      }
    }
    return <table style={{borderCollapse:'collapse', border: borderStyle, width:'100%'}}>
              {result}
            </table>
  }

}

const getWeekUI = function(currentweek){
  const result = []
  result.push(<tr key={"titles_"+currentweek.id} style={{borderTop:'1px solid lightgrey'}}>
                <td style={{fontWeight:isCurrentWeek(currentweek)?'bold':null, borderRight:'1px solid lightgrey', borderBottom:'1px solid grey',  width:'5%'}} rowspan="2">{currentweek.startDay}</td>
                {getDaysOfWeekUI(currentweek)}
              </tr>)
  result.push(<tr key={"content_"+currentweek.id} style={{borderBottom:'1px solid grey'}}>
                {getDaysOfWeekContentUI(currentweek)}
              </tr>)
  return result
}

const getDaysOfWeekUI = function(currentweek){
  const result = []
  var offset = 0
  for(var dayOfWeekidx in week){
    result.push(<td key={"dayofweek_"+dayOfWeekidx} style={{borderRight:'1px solid lightgrey', width:'12%', fontWeight:isCurrentDay(currentweek, offset)?'bold':null}}>
                  <div style={{borderBottom: borderStyle, fontStyle: 'italic'}}>{weekFullName[week[dayOfWeekidx]]}</div>
                </td>)
    offset++
  }
  return result
}

const getDaysOfWeekContentUI = function(currentweek){
  const result = []
  for(var dayOfWeekidx in week){
    const dayTasksUI = []
    if(currentweek.days!=null && currentweek.days[week[dayOfWeekidx]]!=null){
      for(var taskidx in currentweek.days[week[dayOfWeekidx]]){
        const task = currentweek.days[week[dayOfWeekidx]][taskidx]
        const backgroundColor = task.finished?'lightgreen':null
        dayTasksUI.push(<div key={"task_"+task.id} style={{border:'1px solid lightgrey', borderRadius:'3px', paddingLeft:'3px', backgroundColor:backgroundColor}}
                                  onMouseEnter={()=>fireEvent('overlay-info', 'show', [task.fullname])}
                                  onMouseOver={(e)=>fireEvent('overlay-info', 'update-pos', [e.nativeEvent.clientX+15, e.nativeEvent.clientY-10])}
                                  onMouseLeave={()=>fireEvent('overlay-info', 'hide')}>
                        <a href='#' onClick={()=>fireEvent('task-modal', 'open', [null, task, true, true])}>{task.title}</a>
                      </div>)
      }
    }
    result.push(<td key={"tasks_content_"+week[dayOfWeekidx]} style={{borderRight:'1px solid lightgrey'}}>
                  {dayTasksUI}
                </td>)
  }
  return result
}

const isCurrentWeek = function(week){
  const tzOffset = new Date(week.startDay).getTimezoneOffset() * 60000
  const todayTime = new Date().getTime() - tzOffset
  const startTimeCur = Date.parse(week.startDay)
  const endTimeCur = Date.parse(week.endDay)+86400000 //86400000 - one day in millisecs
  return todayTime>=startTimeCur && todayTime<endTimeCur
}

const isCurrentDay = function(week, offset){
  const tzOffset = new Date(week.startDay).getTimezoneOffset() * 60000
  const weekTime = Date.parse(week.startDay) //- tzOffset
  const todayTime = new Date().getTime()-tzOffset
  return todayTime>(weekTime+(offset*dayInMilliseconds)) && todayTime<(weekTime+((offset+1)*dayInMilliseconds))
}
