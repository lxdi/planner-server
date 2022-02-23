import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common/common-modal'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from '../../data/data-constants'
import {formatDate, currentDateString, tomorrowDateString, yesterdayDateString} from '../../utils/date-utils'
import {filterOutMemoTask, getOnlyMemoOnDateTasks} from '../../utils/task-utils'


export class ActualTasksModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false}}
    this.state = defaultState()
    registerEvent('actual-tasks-modal', 'open', ()=>this.setState({isOpen:true}))
    registerEvent('actual-tasks-modal', 'close', ()=>this.setState(defaultState()))

    registerReaction('actual-tasks-modal', 'tasks-dao', ['repetition-finished'], ()=>{fireEvent('actual-tasks-modal', 'close')})
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen} title="Actual tasks"
                cancelHandler={()=>fireEvent('actual-tasks-modal', 'close')}
                styleClass='actual-tasks-modal-style'>
            <div>
              {this.state.isOpen?content(this):null}
            </div>
          </CommonModal>
  }
}

const content = function(reactcomp){
  const actual = chkSt(DataConstants.progressRep, 'actual')
  return <div>
            <div style={{border:'1px solid blue', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {currentTasks()}
            </div>
            <div style={{border:'1px solid red', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {outdatedCurrenttasks()}
            </div>
            <div style={{border:'1px solid lightgrey', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {spacedRepetitionsUI(reactcomp, actual)}
            </div>
          </div>
}

const currentTasks = function(){
  return 'TODO' //tasksUI(chkSt('tasks-dao', 'actual-tasks')[100], 'Current tasks')
}

const outdatedCurrenttasks = function(){
  return 'TODO' //tasksUI(chkSt('tasks-dao', 'actual-tasks')[99], 'Outdated tasks')
}

// const tasksUI = function(tasks, title){
//   const result = []
//   for(var i in tasks){
//     result.push(taskLink(tasks[i], true))
//   }
//   return <div>
//             <div>{title}</div>
//             {result}
//           </div>
// }

const spacedRepetitionsUI = function(reactcomp, actual){
  const tdStyle = {padding:'5px'}
  return <div>
            Spaced repetitions
            <table border="1" style={{width:'100%', marginBottom:'5px'}}>
              <tr>
                <td style={Object.assign({border:'1px solid purple'}, tdStyle)}>Memorizing</td>
              </tr>
              <tr>
                <td>
                  {tasksMemoListUI(actual.memoReps)}
                </td>
              </tr>
            </table>

            <table border="1" style={{width:'100%'}}>
              <tr>
                <td style={Object.assign({border:'1px solid red'}, tdStyle)}>2 weeks late</td>
                <td style={Object.assign({border:'1px solid orange'}, tdStyle)}>1 week late</td>
                <td style={Object.assign({border:'1px solid green'}, tdStyle)}>About a week</td>
                <td style={Object.assign({border:'1px solid grey'}, tdStyle)}>Upcoming</td>
              </tr>
              <tr>
                <td>{tasksListUI(actual.twoWeeksLate)}</td>
                <td>{tasksListUI(actual.oneWeekLate)}</td>
                <td>{tasksListUI(actual.aboutWeekReps)}</td>
                <td>{tasksListUI(actual.upcomingReps)}</td>
              </tr>
            </table>
        </div>
}

const tasksMemoListUI = function(reps){
  if(reps.length<1){
    return null
  }
  const result = []

  reps
    .filter(rep => filterMemoRepToShow(rep))
    .forEach(rep => result.push(taskLink(rep)))

  return <div>{result}</div>
}

const tasksListUI = function(reps){
  const result = []
  reps.forEach(rep => result.push(taskLink(rep)))
  return <div> {result}</div>
}

const taskLink = function(rep){
  return <div key={rep.taskid} style={{border:'1px solid lightgrey', borderRadius:'3px', padding:'3px'}}
                      onMouseEnter={()=>fireEvent('overlay-info', 'show', [rep.fullPath])}
  										onMouseOver={(e)=>fireEvent('overlay-info', 'update-pos', [e.nativeEvent.clientX+15, e.nativeEvent.clientY-10])}
  										onMouseLeave={()=>fireEvent('overlay-info', 'hide')}>
            <a href='#' onClick={()=>fireEvent('mean-modal', 'open-with-task', [{id: rep.meanid}, rep.taskid, rep.id])}>{rep.taskFullPath}</a>
            <div style={{fontSize:'9px', fontColor:'lightGrey', marginLeft: '5px', display: 'inline-block'}}>{getIncomingTag(rep)}</div>
      </div>
}

const getIncomingTag = function(rep){
  if(rep == null || rep.planDay == null){
    return null
  }
  if(rep.planDay.date == yesterdayDateString('-')){
    return 'yesterday'
  }
  if(rep.planDay.date == currentDateString('-')){
    return 'today'
  }
  if(rep.planDay.date == tomorrowDateString('-')){
    return 'tomorrow'
  }
  return formatDate(rep.planDay.date)
}

const filterMemoRepToShow = function(rep){
  const tag = getIncomingTag(rep)

  console.log(tag, rep)
  if(tag == 'yesterday' || tag == 'today' || tag == 'tomorrow'){
    return true
  }

  return false
}
