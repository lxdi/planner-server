import React from 'react';
import ReactDOM from 'react-dom';
import {FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import {CommonModal} from './../common-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class SubjectModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {isOpen:null}
    registerEvent('subject-modal', 'open', (stateSetter, layer, subject)=>this.setState({isOpen: true, currentSubject: subject, layer:layer}))
    registerEvent('subject-modal', 'close', ()=>this.setState({isOpen: false, currentSubject: null, layer: null}))
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen}
            okHandler={()=>{fireEvent('subjects-dao', 'add-subject', [this.state.layer, this.state.currentSubject]); fireEvent('subject-modal', 'close')}}>
              {this.state.currentSubject!=null? modalContent(this): null}
            </CommonModal>
  }
}

const modalContent = function(component){
  if(component.state.currentSubject.title == null){
    component.state.currentSubject.title = ''
  }
  return   <form>
                    <FormGroup controlId="formBasicText">
                    <ControlLabel>Title</ControlLabel>
                    <FormControl
                        type="text"
                        value={component.state.currentSubject.title}
                        placeholder="Enter title"
                        onChange={(e)=>{component.state.currentSubject.title = e.target.value; component.setState({})}}/>
                    </FormGroup>
                  </form>
}
