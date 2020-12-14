import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'

import {formatDate, currentDateString, tomorrowDateString, yesterdayDateString} from '../../utils/date-utils'
import {filterOutMemoTask, getOnlyMemoOnDateTasks} from '../../utils/task-utils'

import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

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
  return <div>
            <div style={{border:'1px solid blue', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {currentTasks()}
            </div>
            <div style={{border:'1px solid red', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {outdatedCurrenttasks()}
            </div>
            <div style={{border:'1px solid lightgrey', borderRadius:'10px', padding:'5px', marginTop:'5px'}}>
              {spacedRepetitionsUI(reactcomp)}
            </div>
          </div>
}

const currentTasks = function(){
  return tasksUI(chkSt('tasks-dao', 'actual-tasks')[100], 'Current tasks')
}

const outdatedCurrenttasks = function(){
  return tasksUI(chkSt('tasks-dao', 'actual-tasks')[99], 'Outdated tasks')
}

const tasksUI = function(tasks, title){
  const result = []
  for(var i in tasks){
    result.push(taskLink(tasks[i], true))
  }
  return <div>
            <div>{title}</div>
            {result}
          </div>
}

const spacedRepetitionsUI = function(reactcomp){
  const tdStyle = {padding:'5px'}
  return <div>
            Spaced repetitions
            <table border="1" style={{width:'100%', marginBottom:'5px'}}>
              <tr>
                <td style={Object.assign({border:'1px solid purple'}, tdStyle)}>Memorizing</td>
              </tr>
              <tr>
                <td>
                  {tasksMemoListUI(getOnlyMemoOnDateTasks(chkSt('tasks-dao', 'actual-tasks')[-0], yesterdayDateString('-')), 'yesterday')}
                  {tasksMemoListUI(getOnlyMemoOnDateTasks(chkSt('tasks-dao', 'actual-tasks')[-0], currentDateString('-')), 'today')}
                  {tasksMemoListUI(getOnlyMemoOnDateTasks(chkSt('tasks-dao', 'actual-tasks')[-0], tomorrowDateString('-')), 'tomorrow')}
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
                <td>{tasksListUI(filterOutMemoTask(chkSt('tasks-dao', 'actual-tasks')[-2]))}</td>
                <td>{tasksListUI(filterOutMemoTask(chkSt('tasks-dao', 'actual-tasks')[-1]))}</td>
                <td>{tasksListUI(filterOutMemoTask(chkSt('tasks-dao', 'actual-tasks')[-0]))}</td>
                <td>{tasksListUI(filterOutMemoTask(chkSt('tasks-dao', 'actual-tasks')[1]))}</td>
              </tr>
            </table>
        </div>
}

const tasksMemoListUI = function(tasks, tag){
  if(tasks.length<1){
    return null
  }
  const result = []
  tasks.forEach((task)=>result.push(taskLink(task, false)))
  return <div>{result}</div>
}

const tasksListUI = function(tasks){
  const result = []
  tasks.forEach((task)=>result.push(taskLink(task, false)))
  return <div> {result}</div>
}

const taskLink = function(task, highlight){
  const backgroundColor = highlight && task.finished?'lightgreen':null
  return     <div key={task.id} style={{border:'1px solid lightgrey', borderRadius:'3px', padding:'3px', backgroundColor:backgroundColor}}
                      onMouseEnter={()=>fireEvent('overlay-info', 'show', [task.fullname])}
  										onMouseOver={(e)=>fireEvent('overlay-info', 'update-pos', [e.nativeEvent.clientX+15, e.nativeEvent.clientY-10])}
  										onMouseLeave={()=>fireEvent('overlay-info', 'hide')}>
            <a href='#' onClick={()=>fireEvent('task-modal', 'open', [null, task, true, true])}>{task.title}</a>
            <div style={{fontSize:'9px', fontColor:'lightGrey', marginLeft: '5px', display: 'inline-block'}}>{getIncomingTag(task)}</div>
      </div>
}

const getIncomingTag = function(task){
  if(task.repetition == null || task.repetition.planDate == null){
    return null
  }
  if(task.repetition.planDate == yesterdayDateString('-')){
    return 'yesterday'
  }
  if(task.repetition.planDate == currentDateString('-')){
    return 'today'
  }
  if(task.repetition.planDate == tomorrowDateString('-')){
    return 'tomorrow'
  }
  return formatDate(task.repetition.planDate)
}
