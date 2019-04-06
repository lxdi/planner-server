import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'

import {registerEvent, registerReaction, fireEvent, viewStateVal} from 'absevent'

export class LeftSideBarContent extends React.Component {
  constructor(props){
    super(props)

    registerReaction('actual-tasks-ui', 'tasks-dao', ['actual-tasks-rs'], ()=>this.setState({}))
    registerReaction('actual-tasks-ui', 'main-ui', ['switch-mode'], ()=>this.setState({}))

    registerReaction('actual-tasks-ui', 'tasks-dao', ['repetition-finished'], (stateSetter)=>this.setState({}))
  }

  render(){
    return content(this)
  }
}

const content = function(reactcomp){
  const actualTasksMap = viewStateVal('tasks-dao', 'actual-tasks');
  if(actualTasksMap==null){
    fireEvent('tasks-dao', 'actual-tasks-rq')
    return 'Loading...'
  }
  return <div>
              <a href="#" style={{textDecoration:'none'}}>
                <div onClick={()=>fireEvent('actual-tasks-modal', 'open')} class="actual-tasks-indicators-group">
                  {getSquare(actualTasksMap['100'].length, 'blue')}
                  {divisor()}
                  {getSquare(actualTasksMap['-2'].length, 'red')}
                  {getSquare(actualTasksMap['-1'].length, 'orange')}
                  {getSquare(actualTasksMap['0'].length, 'green')}
                  {getSquare(actualTasksMap['1'].length, 'grey')}
                </div>
              </a>
              {divisor()}
              <div style={{margin:'2px', width:'45px', height:'45px', fontSize:'10pt'}}>
                <button class="left-bar-button" onClick = {()=>fireEvent('main-ui', 'switch-mode')}>
                {viewModeButtonLabel()}
                </button>
              </div>
              {getSwitchButton()}
            </div>
}

const getSquare = function(num, color){
	return <div style={{border:'1px solid '+color, margin:'2px', width:'45px', height:'30px', borderRadius:'8px', textAlign:'center', color:color, fontSize:'13pt'}}>{num}</div>
}

const divisor = function(){
  return <div style={{backgroundColor:'lightgrey', width:'100%', height:'1px', marginLeft:'2px', marginRight:'2px', marginTop:'5px', marginBottom:'5px'}}></div>
}

const getSwitchButton = function(){
  if(!viewStateVal('main-ui', 'three-frames')){
    return <div style={{margin:'2px', width:'45px', height:'45px'}}>
                    <button class="left-bar-button-switch" style={{fontSize:'8pt'}} onClick={()=>fireEvent('main-ui', 'switch-curr-state')}>Switch</button>
                  </div>
  } else {
    return <div></div>
  }
}

const viewModeButtonLabel = function(){
  if(!viewStateVal('main-ui', 'three-frames')){
    return ' |  | '
  } else {
    return <div>
                 <span style={{color:'green'}}> | </span>
                 <span style={{color:'blue'}}> | </span>
                 <span style={{color:'orange'}}> | </span>
            </div>
  }
}
