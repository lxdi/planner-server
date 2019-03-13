import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl, Button} from 'react-bootstrap'
import {CommonModal} from './../common-modal'
import {CommonCrudeTemplate} from './../common-crud-template'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

const createState = function(isOpen, isStatic, isEdit, subject, task, finishing){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit, finishing:finishing},
    subject: subject,
    task: task
  }
}

export class TaskModal extends React.Component {
  constructor(props){
    super(props)
    this.state = createState(false, false, false, null, null)
    registerEvent('task-modal', 'open', (stateSetter, subject, task, isViewOnly, withFinishing)=>this.setState(getState(subject, task, isViewOnly, withFinishing)))
    registerEvent('task-modal', 'close', ()=>this.setState(createState(false, false, false, null, null, null)))

    registerReaction('task-modal', 'tasks-dao', 'task-deleted', (stateSetter)=>fireEvent('task-modal', 'close'))
  }

  render(){
    const isViewOnly = this.state.mode.isStatic && !this.state.mode.isEdit
    return <CommonModal title="Task"
                        isOpen={this.state.isOpen}
                        cancelHandler={()=>fireEvent('task-modal', 'close')}
                        okHandler={!isViewOnly && isTaskValid(this.state.task)?()=>okHandler(this.state.subject, this.state.task):null}
                        styleClass="task-modal-style">
              {this.state.task!=null? modalContent(this): null}
            </CommonModal>
  }
}

const getState = function(subject, task, isViewOnly, withFinishing){
  var state = null
  if(task.id == null || task.id == 0){
    state = createState(true, true, true, subject, task)
  } else {
    state = createState(true, false, false, subject, task)
  }
  if(isViewOnly!=null && isViewOnly==true){
    state.mode.isStatic = true
    state.mode.isEdit = false
  }
  if(withFinishing==true){
    state.mode.finishing = true
  }
  return state
}

const isTaskValid = function(task){
  return task!=null && task.title!=null && task.title!=''
}

const okHandler = function(subject, task){
  if(task.id==null){
    fireEvent('tasks-dao', 'add-task', [subject, task])
    task.id = 0
  }
  fireEvent('task-modal', 'close')
}

var newTopicId = 1

const modalContent = function(component){
  if(component.state.task.title == null){
    component.state.task.title = ''
  }
  return      <CommonCrudeTemplate editing = {component.state.mode}
                  changeEditHandler = {()=>component.setState({})}
                  deleteHandler={()=>fireEvent('tasks-dao', 'delete-task', [component.state.subject, component.state.task])}>
                <form>
                    {component.state.mode.finishing?<Button onClick={()=>fireEvent('finishing-task-modal', 'open', [component.state.task])}>Complete Task</Button>:null}
                    <FormGroup controlId="formBasicText">
                    <div style={{display:'inline-block', paddingRight:'5px'}}><ControlLabel>Title:</ControlLabel></div>
                    <div style={{display:'inline-block'}}>{statefulTextfield(component, component.state.task, 'title')}</div>
                    {topicsUI(component)}
                    </FormGroup>
                  </form>
                </CommonCrudeTemplate>
}

const topicsUI = function(component){
  const result = []
  const commonStyle = {display:'inline-block', paddingLeft:'5px', borderLeft:'1px solid grey'}
  const styleFields = {width:'45%'}
  const styleRemoveLink = {width:'10%'}
  Object.assign(styleFields, commonStyle)
  Object.assign(styleRemoveLink, commonStyle)
  const topics = component.state.task.topics
  for(var indx in topics){
    const topic = topics[indx]
    const key = topic.id==null?topic.tempId:topic.id
    result.push(<div key={key} style={{borderBottom:'1px solid lightgrey', marginBottom:'3px'}}>
                    <div style={styleFields}>{statefulTextfield(component, topic, 'title')}</div>
                    <div style={styleFields}>{statefulTextfield(component, topic, 'source')}</div>
                    <div style={styleRemoveLink}>{removeTopicLink(component, topics, topic)}</div>
                </div>)
  }
  return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
          <div><strong>Topics:</strong></div>
          {result}
          {addTopicButton(component)}
        </div>
}

const statefulTextfield = function(component, obj, valName){
  if(component.state.mode.isEdit){
    return textField(component, obj, valName)
  } else {
    return <FormControl.Static>{obj[valName]}</FormControl.Static>
  }
}

const textField = function(component, obj, valName){
  return <FormControl
      type="text"
      value={obj[valName]}
      placeholder={"Enter "+valName}
      onChange={(e)=>{obj[valName] = e.target.value; component.setState({})}}/>
}

const addTopicButton = function(component){
  if(component.state.mode.isEdit){
    return <Button onClick={()=>{component.state.task.topics.push({tempId:'new_'+newTopicId++, title:'', source:''}); component.setState({})}}>+ Add topic</Button>
  }
}

const removeTopicLink = function(component, topics, topic){
  if(component.state.mode.isEdit){
    return <a href="#" onClick={()=>{topics.splice(topics.indexOf(topic), 1); component.setState({})}}>Remove</a>
  }
}
