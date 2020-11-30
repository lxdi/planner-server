import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from '../../../data/data-constants'
import {CreateLayer, CreateTask} from '../../../data/creators'

// props: layer, isEdit
export class TasksGroup extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    return tasksUI(this.props.layer, this.props.isEdit)
  }
}

const taskCssStyle = {
  display: 'table-cell',
  padding: '5px',
  border: '1px solid lightgrey',
  'vertical-align':'top'}

const tasksUI = function(layer, isEdit){
  const tasksHTML = []
  if(layer.tasks!=null){

    for(var taskPos in layer.tasks){
      const task = layer.tasks[taskPos]
      tasksHTML.push(<div key={'layer_'+layer.priority+'_task_'+taskPos}
                          style={taskCssStyle}
                          draggable={isEdit?"true":"false"}
                          onDragStart={()=>fireEvent(DataConstants.taskRep, 'add-task-to-drag', [subject, task])}
                          onDragEnd={()=>fireEvent(DataConstants.taskRep, 'release-draggable-task')}
                          onDragOver={(e)=>moveEvent(e, layer, task, 'task', isEdit)}>

                          <a href='#' onClick={()=>fireEvent('task-modal', 'open', [layer, task])}>{task.title}</a>

                      </div>)
    }
  }

  if(isEdit){

    tasksHTML.push(<div key={'layer_'+layer.priority+'_task_phantom'}
                        style={taskCssStyle}
                        draggable={isEdit?"true":"false"}
                        onDragOver={(e)=>moveEvent(e, layer, null, 'task', isEdit)}>
                        <span style={{width:'50px'}} />
                    </div>)

    tasksHTML.push(<div key={'layer_'+layer.priority+'_task_toAdd'} style={taskCssStyle}>
                        <Button bsStyle="success" bsSize="xsmall"  onClick={()=>fireEvent('task-modal', 'open', [layer, CreateTask(DataConstants.newId, '', layer.id)])}>
                            +Add task
                        </Button>
                      </div>)
  }

  return tasksHTML
}
