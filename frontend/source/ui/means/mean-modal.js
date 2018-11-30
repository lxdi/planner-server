import {meanModalHeaderTitle, targetsDropDownTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {CreateMean} from './../../data/creators'
import {CommonCrudeTemplate} from './../common-crud-template'
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


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
      this.setState(defaultState())
    }.bind(this))

    registerReaction('means-modal', 'means-dao', ['mean-deleted', 'mean-modified', 'mean-created'], function(){
      fireEvent('mean-modal', 'close')
      this.setState({})
    }.bind(this))
    registerReaction('mean-modal', 'layers-dao', ['layers-received', 'add-layer'], ()=>this.setState({}))
    registerReaction('mean-modal', 'subjects-dao', ['add-subject'], ()=>this.setState({}))
    registerReaction('mean-modal', 'tasks-dao', ['add-task'], ()=>this.setState({}))

  }

  okHandler(){
    if(this.state.currentMean.id==0){
      fireEvent('means-dao', 'create', [this.state.currentMean, this.state.parent])
    } else {
      fireEvent('means-dao', 'modify', [this.state.currentMean])
    }
  }

  handleNameVal(e){
    this.state.currentMean.title = e.target.value;
    this.setState({});
  }

  selectTargetHandler(target, e){
    this.state.currentMean.targets.push(target)
    this.setState({});
  }

  render(){
        return <CommonModal isOpen = {this.state.isOpen} okHandler = {this.okHandler} cancelHandler = {()=>fireEvent('mean-modal', 'close', [])} title={meanModalHeaderTitle} >
            <CommonCrudeTemplate editing = {this.state.mode} changeEditHandler = {this.forceUpdate.bind(this)} deleteHandler={()=>fireEvent('means-dao', 'delete', [this.state.currentMean.id])}>
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
  return viewStateVal('targets-dao', 'targets').map(function(target){
    return <MenuItem eventKey={target}>{target.toString()}</MenuItem>
  }, (target)=>target.realmid==viewStateVal('realms-dao', 'currentRealm').id)
}

const relatedTargetsUI = function(targets){
  return targets.map((target) =>
    <li>{target.toString()}</li>
  );
}

const layersBlock = function(mean, isEdit){
  return <ListGroup>
            <div>
              <h4>Layers</h4>
              {isEdit?<a href='#' onClick={()=>fireEvent('layers-dao', 'add-layer', [mean])}> Create Layer</a>:null}
            </div>
            <ListGroup>
              {layersUI(mean)}
            </ListGroup>
          </ListGroup>
}

const layersUI = function(mean){
    const layersHTML = []
    if(mean.layers!=null && mean.layers.length>0){
        for(var layerPriority in mean.layers){
          const layer = mean.layers[layerPriority]
          layersHTML.push(<ListGroupItem key={'layer_'+layerPriority}>
                              <div>Layer {layer.priority}</div>
                              <div><a href='#' onClick={()=>fireEvent('subject-modal', 'open', [layer, {}])}>Add subject</a></div>
                              <div>{subjectsUI(layer)}</div>
                            </ListGroupItem>)
        }
    }
    return layersHTML
}

const subjectsUI = function(layer){
  const subjectsHTML = []
  if(layer.subjects!=null && layer.subjects.length>0){
    for(var subjectPos in layer.subjects){
      const subject = layer.subjects[subjectPos]
      subjectsHTML.push(<ListGroupItem key={'layer_'+layer.priority+'_subject_'+subjectPos}>
                          {subject.title}
                          <a href='#' onClick={()=>fireEvent('task-modal', 'open', [subject, {}])}> Add task</a>
                          {tasksUI(subject)}
                        </ListGroupItem>)
    }
  }
  return <ListGroup>
          {subjectsHTML}
        </ListGroup>
}

const tasksUI = function(subject){
  const tasksHTML = []
  if(subject.tasks!=null && subject.tasks.length>0){
    for(var taskPos in subject.tasks){
      const task = subject.tasks[taskPos]
      tasksHTML.push(<ListGroupItem key={'subject_'+subject.priority+'_task_'+taskPos}>
                          {task.title}
                        </ListGroupItem>)
    }
  }
  return <ListGroup>
          {tasksHTML}
        </ListGroup>
}
