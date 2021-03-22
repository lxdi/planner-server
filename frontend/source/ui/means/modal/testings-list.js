import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'

import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {makeMap} from 'js-utils'

import {TextField} from '../../common/text-field'
import {TextArea} from '../../common/text-area'
import {StatefulTextField} from '../../common/stateful-text-field'
import {TreeComponent} from './../../components/tree-component'
import {DataConstants} from '../../../data/data-constants'


var newTestingId = 1

//props: task, isEdit, testingsGuesses
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
  const testings = component.props.task.taskTestings
  const testingsMap = makeMap(testings, 'id')
  // for(var indx in testings){
  //   const testing = testings[indx]
  //
  //   result.push(<div key={testing.id} style={{borderBottom:'1px solid lightgrey', marginBottom:'3px'}}>
  //               <div>
  //                   <div style={styleFields}>
  //                     <StatefulTextField obj={testing} valName={'question'} isEdit={component.props.isEdit}>
  //                       {testing.question}<a href='#' onClick={()=>{testing.answer=''; component.setState({})}} >(answer)</a>
  //                     </StatefulTextField>
  //                   </div>
  //                   <div style={styleRemoveLink}>{removeTestingLink(component, testings, testing)}</div>
  //               </div>
  //               <div>
  //                 {testing.answer!=null?<TextArea obj={testing} valName={'answer'}/>:null}
  //               </div>
  //             </div>)
  // }
  //
  // return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
  //         <div><strong>Testings:</strong></div>
  //         {component.props.testingsGuesses!=null && component.props.testingsGuesses!=''?<TextArea obj={component.props} valName={'testingsGuesses'} valNameUI={'objectives'} readOnly={true}/>:null}
  //         {result}
  //         {addTestingButton(component)}
  //       </div>

  return <div style={{border:'1px solid lightgrey', padding:'5px', borderRadius:'10px'}}>
            <div><strong>Testings:</strong></div>
            {component.props.testingsGuesses!=null && component.props.testingsGuesses!=''?<TextArea obj={component.props} valName={'testingsGuesses'} valNameUI={'objectives'} readOnly={true}/>:null}
            <Button onClick={()=>fireEvent('testing-modal', 'open', [component.props.task, {parentid:null}])}>+ Add testing</Button>

            <TreeComponent isEdit={component.props.isEdit}
                              nodes={testingsMap}
                              viewCallback = {(testing)=>testingUI(component, testing)}
                              rootStyle={{border:'1px solid lightgrey', borderRadius:'5px', marginBottom:'5px', padding:'3px'}}
                              shiftpx={15}
                              />
        </div>
}

const testingUI = function(comp, testing){
  return <div>
            {testing.question}
            <a href='#' onClick={()=>fireEvent('testing-modal', 'open', [comp.props.task, {parentid: testing.id}])}> + </a>
        </div>
}

// const addTestingButton = function(component){
//   if(component.props.isEdit){
//     return <Button onClick={()=>addTestingHandler(component)}>+ Add testing</Button>
//   }
// }
//
// const addTestingHandler = function(component){
//   const task = component.props.task
//
//   if(task.taskTestings == null){
//     task.taskTestings = []
//   }
//
//   task.taskTestings.push({
//     id: DataConstants.newId+newTestingId++,
//     question: '',
//     taskid: component.props.task.id
//   })
//
//   component.setState({})
// }

// const removeTestingLink = function(component, testings, testing){
//   if(component.props.isEdit){
//     return <a href="#" onClick={()=>{testings.splice(testings.indexOf(testing), 1); component.setState({})}}>Remove</a>
//   }
// }
