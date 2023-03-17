import React from 'react';
import ReactDOM from 'react-dom';

import {Button} from 'react-bootstrap'

import {CommonModal} from './common-modal'

const defaultState = function(){
  return {
    edit:true
  }
}

// editing{isStatic isEdit} changeEditHandler deleteHandler
//isStatic means a view only mode
export class CommonCrudeTemplate extends React.Component {
  constructor(props){
    super(props)
    this.state = defaultState()
  }

  render(){
    return (
        <div>
          {!this.props.editing.isStatic?
            <Button bsSize="xsmall" onClick={() => editAbilityHandler(this)}>
              {this.props.editing.isEdit? 'Save': 'Edit'}
            </Button>
          : null}

          {!this.props.editing.isStatic && this.props.editing.isEdit && this.props.deleteHandler!=null?
            <Button bsStyle="danger" bsSize="xsmall" onClick={this.props.deleteHandler}>
              Delete
            </Button>
          : null}

          {this.props.children}
      </div>
    )
  }
}

const editAbilityHandler = function(comp){
  comp.props.editing.isEdit = !comp.props.editing.isEdit
  comp.setState({})
  comp.props.changeEditHandler()
}
