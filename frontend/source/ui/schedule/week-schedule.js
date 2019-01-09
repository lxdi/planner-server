import React from 'react';
import ReactDOM from 'react-dom';
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

const week = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']
const weekFullName = {'mon': 'Monday', 'tue': 'Tuesday', 'wed': 'Wednsday', 'thu':'Thursday', 'fri': 'Friday', 'sat': 'Saturday', 'sun':'Sunday'}

//props: hquarter
export class WeekSchedule extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    const borderStyle = '1px solid lightgrey'
    const result = []
    const hquarter = this.props.hquarter
    if(hquarter.weeks!=null && hquarter.weeks.length>0){
      for(var i in hquarter.weeks){
        const weekUI = []
        weekUI.push(<td style={isCurrentWeek(hquarter.weeks, i)?{fontWeight:'bold'}:{}}>{hquarter.weeks[i].startDay}</td>)
        for(var dayOfWeekidx in week){
          const weekDayUI = []
          weekDayUI.push(<div style={{borderBottom: borderStyle, fontStyle: 'italic'}}>{weekFullName[week[dayOfWeekidx]]}</div>)
          if(hquarter.weeks[i].days!=null && hquarter.weeks[i].days[week[dayOfWeekidx]]!=null){
            for(var taskidx in hquarter.weeks[i].days[week[dayOfWeekidx]]){
              const task = hquarter.weeks[i].days[week[dayOfWeekidx]][taskidx]
              weekDayUI.push(<li>
                              <a href='#' onClick={()=>fireEvent('task-modal', 'open', [null, task, true])}>{task.title}</a>
                            </li>)
            }
          }
          weekUI.push(<td style={{padding:'3px', border: borderStyle, verticalAlign: 'top'}}>{weekDayUI}</td>)
        }
        result.push(<table style={{borderCollapse:'collapse', border: borderStyle}}><tr>{weekUI}</tr></table>)
      }
    }
    return result
  }

}

const isCurrentWeek = function(weeks, icur){
  const todayTime = new Date().getTime()
  const startTimeCur = Date.parse(weeks[icur].startDay)
  const inext = parseInt(icur)+1
  const startTimeNext = weeks[inext]!=null? Date.parse(weeks[inext].startDay):0
  return todayTime>=startTimeCur && todayTime<startTimeNext
}
