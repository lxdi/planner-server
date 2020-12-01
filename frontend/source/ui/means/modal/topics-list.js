import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'

import {StatefulTextField} from '../../common/stateful-text-field'
import {DataConstants} from '../../../data/data-constants'

var newTopicId = 1

//props: task, isEdit
export class TopicsList extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    const result = []
    const commonStyle = {display:'inline-block', paddingLeft:'5px', borderLeft:'1px solid grey'}
    const styleFields = {width:'45%'}
    const styleRemoveLink = {width:'10%'}
    Object.assign(styleFields, commonStyle)
    Object.assign(styleRemoveLink, commonStyle)

    if(this.props.task.topics==null){
      this.props.task.topics = []
    }

    const topics = this.props.task.topics

    for(var indx in topics){
      const topic = topics[indx]
      const key = topic.id
      result.push(<div key={key} style={{borderBottom:'1px solid lightgrey', marginBottom:'3px'}}>
                      <div style={styleFields}><StatefulTextField obj={topic} valName={'title'} isEdit={this.props.isEdit}/></div>
                      <div style={styleFields}><StatefulTextField obj={topic} valName={'source'} isEdit={this.props.isEdit}/></div>
                      <div style={styleRemoveLink}>{removeTopicLink(this)}</div>
                  </div>)
    }

    return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
            <div><strong>Topics:</strong></div>
            {result}
            {addTopicButton(this)}
          </div>
  }
}

const addTopicButton = function(component){
  if(component.props.isEdit){
    return <Button onClick={() => addTopicHandler(component)}>+ Add topic</Button>
  }
}

const addTopicHandler = function(component){
  const task = component.props.task

  if(task.topics == null){
    task.topics = []
  }

  task.topics.push({
    id: DataConstants.newId+newTopicId++,
    title: '',
    source: '',
    taskid: component.props.task.id
  })

  component.setState({})
}

const removeTopicLink = function(component, topics, topic){
  if(component.props.isEdit){
    const topics = component.props.task.topics
    return <a href="#" onClick={()=>{topics.splice(topics.indexOf(topic), 1); component.setState({})}}>Remove</a>
  }
}
