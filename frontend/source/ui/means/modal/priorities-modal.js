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

    registerReaction('priorities-modal', 'mean-rep', ['got-with-priority'], ()=>this.setState({}))

  }

  render(){
    var content = null

    if(this.state.isOpen){
      content = getConent(this)
    }

    return <CommonModal isOpen = {this.state.isOpen} cancelHandler={()=>fireEvent('priorities-modal', 'close')} title={"Priorities"} >
          {content}
      </CommonModal>
  }
}

const getConent = function(comp){
  const means = chkSt('mean-rep', 'index-with-priorites')

  if(means == null){
    fireEvent('mean-rep', 'get-with-priority')
    return 'Loading...'
  }

  const result = []

  means.forEach(mean => {

    if(mean.layers == null){
      return;
    }

    mean.layers.forEach(layer => {
      result.push( <tr id={layer.id}>
                      <td>{mean.title}</td>
                      <td>{layer.depth}</td>
                    </tr>)
    })
  })

  return  <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>Mean</td>
                <td>Layer</td>
              </tr>
              {result}
            </tbody>
            </Table>

}
