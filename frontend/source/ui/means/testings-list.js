import React from 'react';
import ReactDOM from 'react-dom';
import {FormControl, Button} from 'react-bootstrap'


var newTestingId = 1

//props: testings, isEdit
export class TestingsList extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    return content(this)
  }
}

const content = function(component){
  const result = []
  const commonStyle = {display:'inline-block', paddingLeft:'5px', borderLeft:'1px solid grey'}
  const styleFields = {width:'45%'}
  const styleRemoveLink = {width:'10%'}
  Object.assign(styleFields, commonStyle)
  Object.assign(styleRemoveLink, commonStyle)
  const testings = component.props.testings
  for(var indx in testings){
    const testing = testings[indx]
    const key = testing.id==null?testing.tempId:testing.id
    result.push(<div key={key} style={{borderBottom:'1px solid lightgrey', marginBottom:'3px'}}>
                    <div style={styleFields}>{statefulTextfield(component, testing, 'question')}</div>
                    <div style={styleRemoveLink}>{removeTestingLink(component, testings, testing)}</div>
                </div>)
  }
  return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
          <div><strong>Testings:</strong></div>
          {result}
          {addTestingButton(component)}
        </div>
}

const addTestingButton = function(component){
  if(component.props.isEdit){
    return <Button onClick={()=>{component.props.testings.push({tempId:'new_'+newTestingId++, question:''}); component.setState({})}}>+ Add testing</Button>
  }
}

const removeTestingLink = function(component, testings, testing){
  if(component.props.isEdit){
    return <a href="#" onClick={()=>{testings.splice(testings.indexOf(testing), 1); component.setState({})}}>Remove</a>
  }
}

const statefulTextfield = function(component, obj, valName){
  if(component.props.isEdit){
    return textField(component, obj, valName)
  } else {
    return <FormControl.Static>{obj[valName]}</FormControl.Static>
  }
}

const textField = function(component, obj, valName){
  return <FormControl
      type="text"
      value={obj[valName]}
      placeholder={"Enter "+valName}
      onChange={(e)=>{obj[valName] = e.target.value; component.setState({})}}/>
}
