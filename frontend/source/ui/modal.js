import React from 'react';
import ReactDOM from 'react-dom';
import {Modal, Button} from 'react-bootstrap'

export class CommonModal extends React.Component{
  render(){
    return (
      <Modal show={this.props.isOpen}>
            <Modal.Header>
              <Modal.Title>{this.props.title}</Modal.Title>
            </Modal.Header>
            {this.props.children}
            <Modal.Footer>
              <Button onClick={this.props.cancelHandler}>Close</Button>
              <Button bsStyle="primary" onClick={this.props.okHandler} >Create</Button>
            </Modal.Footer>
      </Modal>
    )
  }
}
