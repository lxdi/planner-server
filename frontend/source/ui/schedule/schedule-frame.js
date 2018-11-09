import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {TaskModal} from './task-modal'
import {AllWeeks} from './../../data/weeks-dao'
import {AllTasks, TaskWithWeek, AddTask, CreateTask, DeleteTaskById, UpdateTask} from './../../data/tasks-dao'
import {CurrentDate} from './../../state'

const defaultState = function(){
  return {
    isTaskModalOpen:false,
    currentWeek: null,
    currentTask: CreateTask(0, '', null),
    modalMode: {isStatic: true, isEdit: true}
  }
}

export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = defaultState();
    this.createTaskOnWeek = this.createTaskOnWeek.bind(this);
    this.openExistingTask = this.openExistingTask.bind(this);
    this.closeTaskModalHandler = this.closeTaskModalHandler.bind(this);

    var uiUpdate = function(){
      this.setState({})
    }.bind(this)

    AllWeeks(uiUpdate)
    AllTasks(uiUpdate)
  }

  //Create task that is related to the week
  createTaskOnWeek(week){
    this.setState({
      isTaskModalOpen:true,
      currentWeek: week,
      currentTask: CreateTask(0, '', null),
      modalMode: {isStatic: true, isEdit: true}
    })
  }

  //Open existing task in modal
  openExistingTask(task, week){
    this.setState({
      isTaskModalOpen:true,
      currentWeek: week,
      currentTask: task,
      modalMode: {isStatic: false, isEdit: false}
    })
  }

  closeTaskModalHandler(eventtype, task, week){
    if(eventtype == "create"){
      AddTask(task, this.state.currentWeek, function(){
        this.setState(defaultState())
      }.bind(this))
    }
    if(eventtype == "delete"){
      DeleteTaskById(task.id, function(){
        this.setState(defaultState())
      }.bind(this))
    }
    if(eventtype == "update"){
      UpdateTask(task, function(){
        this.setState(defaultState())
      }.bind(this))
    }
    this.setState(defaultState())
  }

  render(){
    return(
      <div>
        <div>
          {<TaskModal  isOpen={this.state.isTaskModalOpen} closeCallback={this.closeTaskModalHandler} currentTask={this.state.currentTask} mode={this.state.modalMode} />}
        </div>
        <div>
        <Table striped bordered condensed hover>
          <tbody>
            {weeksUI(this.createTaskOnWeek, this.openExistingTask)}
          </tbody>
        </Table>
        </div>
      </div>
    )
  }
}

const weeksUI = function(createTaskHandler, openTaskHandler){
  var weeks2018 = AllWeeks()[2018]
  if(weeks2018!=null){
    return weeks2018.map((week) =>
      <tr>
        <td style = {{width:'100px'}}>
          {isCurrentWeek(week)?
            <span style={{'font-weight': 'bold'}}>{formatWeekDates(week)}</span>
            : formatWeekDates(week)}
        </td>
        <td>
          {weekTaskUI(week, createTaskHandler, openTaskHandler)}
        </td>
      </tr>
    );
  }
}

const formatWeekDates = function(week){
  return formatDateNumber(week.startDay) + '.' + formatDateNumber(week.startMonth) + ' - ' + formatDateNumber(week.endDay) + '.' + formatDateNumber(week.endMonth)
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

// {weekTaskUI(week, openTaskModalCallback)}
const weekTaskUI = function(week, createTaskHandler, openTaskHandler){
  var task = TaskWithWeek(week.id)
  if(task == null){
    return <Button bsStyle="warning" bsSize="xsmall" onClick={createTaskHandler.bind(this, week)}>
            {addTaskButtonTitle}
            </Button>
  } else {
    return <div>
              <a href="#" onClick={openTaskHandler.bind(this, task, week)}> {task.title} </a>
            </div>
  }
}
