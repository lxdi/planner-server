import React from 'react';
import ReactDOM from 'react-dom';

import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

export class ActualTasks extends React.Component {
  constructor(props){
    super(props)

    registerReaction('actual-tasks-ui', 'tasks-dao', ['actual-tasks-rs'], ()=>this.setState({}))
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
              {getSquare(0, 'blue')}
              <div style={{backgroundColor:'lightgrey', margin:'2px', width:'100%', height:'2px'}}></div>
              {getSquare(actualTasksMap['-2'].length, 'red')}
              {getSquare(actualTasksMap['-1'].length, 'orange')}
              {getSquare(actualTasksMap['0'].length, 'green')}
              {getSquare(actualTasksMap['1'].length, 'grey')}
            </div>
}

const getSquare = function(num, color){
	return <div style={{border:'1px solid '+color, margin:'2px', width:'45px', height:'45px', borderRadius:'8px', textAlign:'center', color:color, fontSize:'13pt'}}>{num}</div>
}
