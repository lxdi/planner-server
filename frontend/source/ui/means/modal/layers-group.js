import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {TasksGroup} from './tasks-group'
import {DataConstants} from '../../../data/data-constants'
import {CreateLayer, CreateTask} from '../../../data/creators'

const PRIORITY_SET = 9999

// props: mean, isEdit
export class LayersGroup extends React.Component {
  constructor(props){
    super(props)

  }

  render(){
    return layersBlock(this, this.props.mean, this.props.isEdit)
  }
}

const layersBlock = function(component, mean, isEdit){
    return <ListGroup>
              <div>
                <h4>Layers</h4>
                {isEdit? getCreateLayerButton(component, mean): null}
              </div>
              <ListGroup>
                {listLayersGroupContent(component, mean, isEdit)}
              </ListGroup>
            </ListGroup>
}

const getCreateLayerButton = function(component, mean){
  return <Button bsStyle="primary" bsSize="xsmall"  onClick={()=>{addNewLayerToMean(mean); component.setState({})}}>
                              Create layer
                          </Button>
}

const listLayersGroupContent = function(comp, mean, isEdit){
    const result = []

    if (mean.layers == null) {
        mean.layers = []
    }

    mean.layers.sort((l1, l2) => l1.depth - l2.depth)

    for(var id in mean.layers){
      const layer = mean.layers[id]

      result.push(<ListGroupItem key={'layer_'+layer.depth}>
                              <div>{layerTitleUI(comp, layer)}</div>
                              <div>
                                <TasksGroup layer = {layer} isEdit = {isEdit} />
                              </div>
                            </ListGroupItem>)
    }
    return result
}

const layerTitleUI = function(comp, layer){
  if(comp.props.isEdit != true){
    return
  }

  return <div>
                <span style={{fontWeight:'bold', fontSize:'12pt'}}>Layer {layer.depth} </span>
                {layer.priority==0? <a href='#' onClick={()=>{layer.priority=PRIORITY_SET; comp.setState({})}}>Prioritize</a>: ''}
          </div>
}

const addNewLayerToMean = function(mean){
  if(mean.layers==null){
    mean.layers = []
  }

  var depth = 0
  for(const id in mean.layers){
    if(mean.layers[id].depth>depth){
      depth = mean.layers[id].depth
    }
  }
  depth = depth + 1
  mean.layers.push(CreateLayer('new', depth, mean.id))
}
