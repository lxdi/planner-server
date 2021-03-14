import React from 'react';
import ReactDOM from 'react-dom';
import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl,  ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {TasksGroup} from './tasks-group'
import {DataConstants} from '../../../data/data-constants'
import {CreateLayer, CreateTask} from '../../../data/creators'
import {addNewLayerToMean} from '../../../data/mean-loader'

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
                {listLayersGroupContent(mean, isEdit)}
              </ListGroup>
            </ListGroup>
}

const getCreateLayerButton = function(component, mean){
  return <Button bsStyle="primary" bsSize="xsmall"  onClick={()=>{addNewLayerToMean(mean); component.setState({})}}>
                              Create layer
                          </Button>
}

const listLayersGroupContent = function(mean, isEdit){
    const result = []

    for(var id in mean.layers){
      const layer = mean.layers[id]
      result.push(<ListGroupItem key={'layer_'+layer.priority}>
                              <div style={{fontWeight:'bold', fontSize:'12pt'}}>Layer {layer.priority}</div>
                              <div>
                                <TasksGroup layer = {layer} isEdit = {isEdit} />
                              </div>
                            </ListGroupItem>)
    }
    return result
}
