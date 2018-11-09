import {modalOkTitle, modalCancelTitle} from './../titles'
import React from 'react';
import ReactDOM from 'react-dom';
// import Modal from 'react-modal';
import {Modal, Button} from 'react-bootstrap'

export class CommonModal extends React.Component{
  render(){
    return (
      <Modal show={this.props.isOpen}>
            <Modal.Header>
              <Modal.Title>{this.props.title}</Modal.Title>
            </Modal.Header>
            <div style={{margin:'5px'}}>
              {this.props.children}
            </div>
            <Modal.Footer>
              <Button onClick={this.props.cancelHandler}>{modalCancelTitle}</Button>
              <Button bsStyle="primary" onClick={this.props.okHandler} >{modalOkTitle}</Button>
            </Modal.Footer>
      </Modal>
    )
  }
}
