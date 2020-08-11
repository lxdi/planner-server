import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {TextField} from '../common/text-field'
import {TextArea} from '../common/text-area'
import {StatefulTextField} from '../common/stateful-text-field'


var newTestingId = 1

//props: testings, isEdit, testingsGuesses
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
                <div>
                    <div style={styleFields}>
                      <StatefulTextField obj={testing} valName={'question'} isEdit={component.props.isEdit}>
                        {testing.question}<a href='#' onClick={()=>{testing.answer=''; component.setState({})}} >(answer)</a>
                      </StatefulTextField>
                    </div>
                    <div style={styleRemoveLink}>{removeTestingLink(component, testings, testing)}</div>
                </div>
                <div>
                  {testing.answer!=null?<TextArea obj={testing} valName={'answer'}/>:null}
                </div>
              </div>)
  }
  return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
          <div><strong>Testings:</strong></div>
          {component.props.testingsGuesses!=null && component.props.testingsGuesses!=''?<TextArea obj={component.props} valName={'testingsGuesses'} valNameUI={'objectives'} readOnly={true}/>:null}
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
