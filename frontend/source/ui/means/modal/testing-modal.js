import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {CommonModal} from './../../common/common-modal'
import {TextField} from '../../common/text-field'


const newIdPrefix = 'new-' //duplicated on the backend

// props: task, testing
export class TestingModal extends React.Component {
  constructor(props){
    super(props)
    const defaultState = {isOpen:false, task: null, testing: null}
    this.state = defaultState

    registerEvent('testing-modal', 'open', (stateSetter, task, testing)=>this.setState({isOpen:true, task:task, testing: testing}))
    registerEvent('testing-modal', 'close', (stateSetter)=>this.setState(defaultState))

  }

  render(){
    return  <CommonModal
                    isOpen = {this.state.isOpen}
                    okHandler = {()=>okHandler(this)}
                    cancelHandler = {()=>fireEvent('testing-modal', 'close')}
                    title={'Add/change Task-testing'}>
                    {getContent(this)}
              </CommonModal>
  }
}

const okHandler = function(comp){
  const task = comp.state.task
  const testing = comp.state.testing

  if(task.taskTestings==null){
    task.taskTestings = []
  }

  if(!task.taskTestings.includes(testing)){
    task.taskTestings.push(testing)
  }

  if(testing.id == null){
    testing.id = newIdPrefix+makeId(10)
    const prevTesting = findLast(task.taskTestings, testing.parentid)

    if(prevTesting!=null && prevTesting!=testing){
      prevTesting.nextid = testing.id
    }
  }
  fireEvent('testing-modal', 'close')
}

const deleteHandler = function(taskTestings, testing){
  deleteTesting(taskTestings, testing)
  fireEvent('testing-modal', 'close')
}

const deleteTesting = function(taskTestings, testing){
  for(var i in taskTestings){
    if(taskTestings[i].parentid==testing.id){
      deleteTesting(taskTestings, taskTestings[i])
    }
  }
  if(taskTestings.includes(testing)){
    const idx = taskTestings.indexOf(testing)
    taskTestings.splice(idx, 1)
    const prevTesting = findPrev(taskTestings, testing)

    if(testing.nextid !=null && prevTesting!=null && prevTesting!=testing){
      prevTesting.nextid = testing.nextid
    }
  }
}

const getContent = function(comp){
  const testing = comp.state.testing
  if(testing == null){
    return null
  }

  if(testing.question == null){
    testing.question = ''
  }
  return <div>
          <div style={{marginBottom: '2px'}}>
            {comp.state.testing.id!=null?
            <Button bsStyle="danger" bsSize="xsmall"
                onClick={()=>fireEvent('confirm-modal', 'open', ['Remove testing?', ()=>deleteHandler(comp.state.task.taskTestings, comp.state.testing)])}>
              Delete
            </Button>:null}
          </div>
          <TextField obj={testing} valName={'question'} />
        </div>
}

const findLast = function(nodes, parentid){
  for(var i in nodes){
    if(nodes[i].parentid == parentid && nodes[i].nextid == null){
      return nodes[i]
    }
  }
  return null
}

const findPrev = function(nodes, node){
  for(var i in nodes){
    if(nodes[i].parentid == node.parentid && nodes[i].nextid == node.id){
      return nodes[i]
    }
  }
  return null
}

function makeId(length) {
   var result           = '';
   var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
   var charactersLength = characters.length;
   for ( var i = 0; i < length; i++ ) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
   }
   return result;
}
