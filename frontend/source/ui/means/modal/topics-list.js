import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {StatefulTextField} from '../../common/stateful-text-field'

var newTopicId = 1

//props: topics, isEdit
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
    const topics = this.props.topics
    for(var indx in topics){
      const topic = topics[indx]
      const key = topic.id==null?topic.tempId:topic.id
      result.push(<div key={key} style={{borderBottom:'1px solid lightgrey', marginBottom:'3px'}}>
                      <div style={styleFields}><StatefulTextField obj={topic} valName={'title'} isEdit={this.props.isEdit}/></div>
                      <div style={styleFields}><StatefulTextField obj={topic} valName={'source'} isEdit={this.props.isEdit}/></div>
                      <div style={styleRemoveLink}>{removeTopicLink(this, topics, topic)}</div>
                  </div>)
    }
    return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
            <div><strong>Topics:</strong></div>
            {result}
            {addTopicButton(this, this.props.topics)}
          </div>
  }
}

const addTopicButton = function(component, topics){
  if(component.props.isEdit){
    return <Button onClick={()=>{topics.push({tempId:'new_'+newTopicId++, title:'', source:''}); component.setState({})}}>+ Add topic</Button>
  }
}

const removeTopicLink = function(component, topics, topic){
  if(component.props.isEdit){
    return <a href="#" onClick={()=>{topics.splice(topics.indexOf(topic), 1); component.setState({})}}>Remove</a>
  }
}
