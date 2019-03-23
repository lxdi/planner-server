import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

export class ActualTasksModal extends React.Component{
  constructor(props){
    super(props)
    const defaultState = ()=>{return {isOpen:false}}
    this.state = defaultState()
    registerEvent('actual-tasks-modal', 'open', ()=>this.setState({isOpen:true}))
    registerEvent('actual-tasks-modal', 'close', ()=>this.setState(defaultState()))

    registerReaction('actual-tasks-modal', 'tasks-dao', ['repetition-finished'], ()=>{fireEvent('actual-tasks-modal', 'close')})
  }

  render(){
    return <CommonModal isOpen={this.state.isOpen} title="Actual tasks"
                cancelHandler={()=>fireEvent('actual-tasks-modal', 'close')}>
            <div>
              {this.state.isOpen?content(this):null}
            </div>
          </CommonModal>
  }
}

const content = function(reactcomp){
  const tdStyle = {padding:'5px'}
  return <div>
            Spaced repetitions
            <table border="1" style={{width:'100%'}}>
              <tr>
                <td style={Object.assign({border:'1px solid red'}, tdStyle)}>2 weeks late</td>
                <td style={Object.assign({border:'1px solid orange'}, tdStyle)}>1 week late</td>
                <td style={Object.assign({border:'1px solid green'}, tdStyle)}>About a week</td>
                <td style={Object.assign({border:'1px solid grey'}, tdStyle)}>Upcoming</td>
              </tr>
              <tr>
                <td>{tasksListUI(viewStateVal('tasks-dao', 'actual-tasks')[-2])}</td>
                <td>{tasksListUI(viewStateVal('tasks-dao', 'actual-tasks')[-1])}</td>
                <td>{tasksListUI(viewStateVal('tasks-dao', 'actual-tasks')[-0])}</td>
                <td>{tasksListUI(viewStateVal('tasks-dao', 'actual-tasks')[1])}</td>
              </tr>
            </table>
        </div>
}

const tasksListUI = function(tasks){
  const result = []
  tasks.forEach((task)=>result.push(
    <div key={task.id}>
          <a href='#' onClick={()=>fireEvent('task-modal', 'open', [null, task, true, true])}>{task.title}</a>
    </div>))
  return <div> {result}</div>
}
