import React from 'react';
import ReactDOM from 'react-dom';
import {Table} from 'react-bootstrap'
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

const getConent = function(comp){
  const orders = chkSt('layer-order-rep', 'objects')

  if(orders == null){
    fireEvent('layer-order-rep', 'get-all')
    return 'Loading...'
  }

  return  <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>Task</td>
                <td>Plan date</td>
                <td>Finish date</td>
                <td></td>
              </tr>
              {result}
            </tbody>
            </Table>

}
