import {meanModalHeaderTitle, targetsDropDownTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CreateMean} from './../../data/creators'
import {CommonCrudeTemplate} from './../common-crud-template'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {SubjectModal} from './subject-modal'
import {TaskModal} from './task-modal'
import {isValidMean} from '../../utils/mean-validator'
import {iterateLLfull} from '../../utils/linked-list'
import {iterateTree} from '../../utils/tree-utils'

const dumbMean = CreateMean(0, '', null, [])

const createState = function(isOpen, isStatic, isEdit, currentMean, parent){
  return {
    isOpen: isOpen,
    mode: {isStatic: isStatic, isEdit: isEdit},
    currentMean: currentMean,
    parent: parent
  }
}

const defaultState = function(){
  return createState(false, true, false, dumbMean, null)
}

export class MeanModal extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState();
    this.okHandler = this.okHandler.bind(this);
    this.handleNameVal = this.handleNameVal.bind(this);
    this.selectTargetHandler = this.selectTargetHandler.bind(this);

    registerEvent('mean-modal', 'open', function(stateSetter, currentMean, parent){
      this.setState(createState(true, currentMean.id==0, currentMean.id==0, currentMean, parent))
      return currentMean
    }.bind(this))

    registerEvent('mean-modal', 'close', function(){
      this.state.currentMean.isFull = false
      this.setState(defaultState())
    }.bind(this))

    registerReaction('means-modal', 'means-dao', ['mean-deleted', 'mean-modified', 'mean-created'], function(){
      fireEvent('mean-modal', 'close')
      this.setState({})
    }.bind(this))
    registerReaction('mean-modal', 'layers-dao', ['layers-received', 'add-layer'], ()=>this.setState({}))
    registerReaction('mean-modal', 'means-dao', 'got-full', ()=>this.setState({}))
    registerReaction('mean-modal', 'subject-modal', 'close', ()=>this.setState({}))
    registerReaction('mean-modal', 'task-modal', 'close', ()=>this.setState({}))
    registerReaction('mean-modal', 'tasks-dao', 'move-task', ()=>this.setState({}))
    registerReaction('mean-modal', 'subjects-dao', 'move-subject', ()=>this.setState({}))

  }

  okHandler(){
    if(this.state.currentMean.id==0){
      fireEvent('means-dao', 'create', [this.state.currentMean, this.state.parent])
    } else {
      this.state.currentMean.isFull=false
      fireEvent('means-dao', 'modify', [this.state.currentMean])
    }
  }

  handleNameVal(e){
    this.state.currentMean.title = e.target.value;
    this.setState({});
  }

  selectTargetHandler(target, e){
    if(this.state.currentMean.targetsIds==null){
      this.state.currentMean.targetsIds=[]
    }
    if(this.state.currentMean.targetsIds.indexOf(target.id)<0){
      this.state.currentMean.targets.push(target)
      this.state.currentMean.targetsIds.push(target.id)
    }
    this.setState({});
  }

  render(){
        this.state.alerts = []
        return <CommonModal
                  isOpen = {this.state.isOpen}
                  okHandler = {isValidMean(this.state.alerts, this.state.currentMean)?this.okHandler:null}
                  cancelHandler = {()=>fireEvent('mean-modal', 'close', [])}
                  title={meanModalHeaderTitle}
                  styleClass='mean-modal-style'>
            <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent('means-dao', 'delete', [this.state.currentMean.id])}>
                {showAlerts(this.state.alerts)}
                <form>
                  <FormGroup controlId="formBasicText">
                  <ControlLabel>Title</ControlLabel>
                  {this.state.mode.isEdit? <FormControl
                                    type="text"
                                    value={this.state.currentMean.title}
                                    placeholder="Enter title"
                                    onChange={this.handleNameVal}/>
                                  : <FormControl.Static>{this.state.currentMean.title}</FormControl.Static>}
                  </FormGroup>
                </form>
                {targetsChooser(this)}
                <div>
                  {relatedTargetsUI(this.state.currentMean.targets)}
                </div>
                {layersBlock(this.state.currentMean, this.state.mode.isEdit)}
                <SubjectModal/>
                <TaskModal/>
            </CommonCrudeTemplate>
          </CommonModal>
  }
}


const targetsChooser = function(component){
  if(component.state.mode.isEdit){
    return <div>
                <ButtonToolbar>
                  <DropdownButton bsSize="small" title={targetsDropDownTitle} id="dropdown-size-small" onSelect={component.selectTargetHandler}>
                    {availableTargetsUI()}
                  </DropdownButton>
                </ButtonToolbar>
            </div>
  }
}

const availableTargetsUI = function(){
  const result = []
  iterateTree(viewStateVal('targets-dao', 'targets')[viewStateVal('realms-dao', 'currentRealm').id], (target, level)=>{
    const style = {marginLeft: (level*10)+'px'}
    result.push(<MenuItem style={style} eventKey={target}>{target.toString()}</MenuItem>)
  })
  return result
}

const relatedTargetsUI = function(targets){
  return targets.map((target) =>
    <li>{target.toString()}</li>
  );
}

const layersBlock = function(mean, isEdit){
  if(mean.id==0 || mean.id==null || (mean.isFull!=null && mean.isFull==true)){
    var createLayerButton = null
    if(isEdit){
      createLayerButton = <Button bsStyle="primary" bsSize="xsmall"  onClick={()=>fireEvent('layers-dao', 'add-layer', [mean])}>
                                  Create layer
                              </Button>
    }
    return <ListGroup>
              <div>
                <h4>Layers</h4>
                {createLayerButton}
              </div>
              <ListGroup>
                {layersUI(mean, isEdit)}
              </ListGroup>
            </ListGroup>
  } else {
    fireEvent('means-dao', 'get-full', [mean])
    return 'Loading...'
  }
}

const layersUI = function(mean, isEdit){
    const layersHTML = []
    if(mean.layers!=null && mean.layers.length>0){
        for(var layerPriority in mean.layers){
          const layer = mean.layers[layerPriority]
          layersHTML.push(<ListGroupItem key={'layer_'+layerPriority}>
                              <div>Layer {layer.priority}</div>
                              <div>{subjectsUI(layer, isEdit)}</div>
                            </ListGroupItem>)
        }
    }
    return layersHTML
}

const subjectAndTaskStyle = {
  display: 'table-cell',
  padding: '5px',
  border: '1px solid lightgrey',
  'vertical-align':'top'}

const subjectsUI = function(layer, isEdit){
  const subjectsHTML = []
  if(layer.subjects!=null && layer.subjects.length>0){
    for(var subjectPos in layer.subjects){
      const subject = layer.subjects[subjectPos]
      subjectsHTML.push(<div key={'layer_'+layer.priority+'_subject_'+subjectPos}>
                            <div style={subjectAndTaskStyle}
                              draggable={isEdit?"true":"false"}
                              onDragStart={()=>fireEvent('subjects-dao', 'add-subject-to-drag', [layer, subject])}
                              onDragEnd={()=>fireEvent('subjects-dao', 'release-draggable-subject')}
                              onDragOver={(e)=>moveEvent(e, layer, subject, 'subject', isEdit)}>
                              <div><a href='#' onClick={()=>fireEvent('subject-modal', 'open', [layer, subject])}>{subject.title}</a></div>
                            </div>
                            {tasksUI(subject, isEdit)}
                          </div>)
    }
  }
  subjectsHTML.push(<div key={'layer_'+layer.priority+'_subject_phantom'}
                        draggable={isEdit?"true":"false"}
                        onDragOver={(e)=>moveEvent(e, layer, null, 'subject', isEdit)}>
                      <div style={subjectAndTaskStyle}>
                        <div style={{width: '50px', height: '10px'}}/>
                      </div>
                    </div>)
  if(isEdit){
    subjectsHTML.push(<div key={'layer_'+layer.priority+'_subject_forAdd'}>
                          <div style={subjectAndTaskStyle}>
                            <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('subject-modal', 'open', [layer, {}])}>
                                +Add subject
                            </Button>
                          </div>
                        </div>)
  }
  return subjectsHTML
}

const tasksUI = function(subject, isEdit){
  const tasksHTML = []
  if(subject.tasks!=null && subject.tasks.length>0){
    for(var taskPos in subject.tasks){
      const task = subject.tasks[taskPos]
      tasksHTML.push(<div key={'subject_'+subject.priority+'_task_'+taskPos}
                          style={subjectAndTaskStyle}
                          draggable={isEdit?"true":"false"}
                          onDragStart={()=>fireEvent('tasks-dao', 'add-task-to-drag', [subject, task])}
                          onDragEnd={()=>fireEvent('tasks-dao', 'release-draggable-task')}
                          onDragOver={(e)=>moveEvent(e, subject, task, 'task', isEdit)}>
                          <a href='#' onClick={()=>fireEvent('task-modal', 'open', [subject, task])}>{task.title}</a>
                      </div>)
    }
  }
  tasksHTML.push(<div key={'subject_'+subject.priority+'_task_phantom'}
                      style={subjectAndTaskStyle}
                      draggable={isEdit?"true":"false"}
                      onDragOver={(e)=>moveEvent(e, subject, null, 'task', isEdit)}>
                      <span style={{width:'50px'}} />
                  </div>)
  if(isEdit){
    tasksHTML.push(<div key={'subject_'+subject.priority+'_task_toAdd'} style={subjectAndTaskStyle}>
                        <Button bsStyle="success" bsSize="xsmall"  onClick={()=>fireEvent('task-modal', 'open', [subject, {}])}>
                            +Add task
                        </Button>
                      </div>)
  }
  return tasksHTML
}

const moveEvent = function(e, parentObj, obj, type, isEdit){
  if(isEdit){
    e.preventDefault()
    if(type == 'task'){
      const draggableTask = viewStateVal('tasks-dao', 'draggable-task')
      if(draggableTask!=null && draggableTask.task != obj){
        fireEvent('tasks-dao', 'move-task', [parentObj, obj])
      }
    }
    if(type == 'subject'){
      const draggableSubject = viewStateVal('subjects-dao', 'draggable-subject')
      if(draggableSubject!=null && draggableSubject.subject){
        fireEvent('subjects-dao', 'move-subject', [parentObj, obj])
      }
    }
  }
}

const showAlerts = function(alerts){
  if(alerts!=null && alerts.length>0){
    const result = []
    for(var i in alerts){
      result.push(<li>{alerts[i]}</li>)
    }
    return <Alert bsStyle="danger">{result}</Alert>
  }
}
