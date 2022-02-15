import React from 'react';
import ReactDOM from 'react-dom';
//import {Button, ButtonToolbar,  DropdownButton, MenuItem,  FormGroup, ControlLabel, FormControl, Alert} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CommonModal} from './../../common/common-modal'

export class PrioritiesModal extends React.Component {
  constructor(props){
    super(props)

    this.state = {isOpen:false}
    registerEvent('priorities-modal', 'open', (stateSetter) => this.setState({isOpen: true}))
    registerEvent('priorities-modal', 'close', (stateSetter) => this.setState({isOpen:false}))

    //registerReaction('external-task-modal', DataConstants.externalTasksRep, ['created', 'updated'], ()=>this.setState(createState(false, true, false, null)))

  }

  render(){
    var content = null

    if(this.state.isOpen){
      content = 'TEST'
    }

    return <CommonModal isOpen = {this.state.isOpen} cancelHandler={()=>fireEvent('priorities-modal', 'close')} title={"Priorities"} >
          {content}
      </CommonModal>
  }
}
