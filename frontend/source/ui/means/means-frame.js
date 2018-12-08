import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CreateMean} from './../../data/creators'
import {Button, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {sortByField} from '../../utils/import-utils'
import {iterateLLfull} from '../../utils/linked-list'

const offsetVal = 10

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}

    var uiUpdate = function(){
      this.setState({})
    }.bind(this)

    registerReaction('means-frame', 'targets-dao', 'target-deleted', (state, targetid)=>{
      fireEvent('means-dao', 'delete-depended-means', [targetid])
      this.setState({})
    })

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao',
            ['means-received', 'replace-mean', 'mean-created', 'mean-deleted', 'mean-modified', 'modify-list', 'draggable-add-as-child'], ()=>this.setState({}))
  }

  render(){
    return(
      <div>
        {viewStateVal('realms-dao', 'currentRealm')!=null? <div style={{'margin-bottom': '3px'}}>
          <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, [])])}>
            {createNewMeanButtonTitle}
          </Button>
          <MeanModal/>
        </div>:null}
        <div onDrop={()=>{fireEvent('means-dao', 'draggable-save-altered'); fireEvent('means-dao', 'remove-draggable', [])}}
              onDragOver={(e)=>e.preventDefault()}>
          <ListGroup style={{marginBottom: '0px'}}>
            {meansUIlist()}
          </ListGroup>
          <div style={{width:'50px', height: '12px', border: '1px dotted lightgrey'}}
                onDragOver={(e)=>{e.preventDefault();fireEvent('means-dao', 'draggable-add-as-child', [null])}}></div>
        </div>
      </div>
    )
  }
}

const meansUIlist = function(){
    if(viewStateVal('means-dao', 'means')!=null){
      const result = []
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        iterateLLfull(viewStateVal('means-dao', 'root-means-by-realm')[viewStateVal('realms-dao', 'currentRealm').id], (mean)=>{
          result.push(<ListGroupItem key={'mean_'+mean.id}>{meanUI(mean, 20)}</ListGroupItem>)
        })
      }
      return result
    } else {
      return "Loading..."
    }
}

// <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [mean])}
//   draggable='true'
//   onDragStart={()=>fireEvent('means-dao', 'add-draggable', [mean])}
//   onDragOver={(e)=>{e.preventDefault();fireEvent('means-dao', 'replace-mean', [parentMean, mean])}}>
//    {mean.children.length==0?
//       <span style={{'font-weight': 'bold'}}>{mean.title}</span>
//       :mean.title}
//  </a>

const meanUI = function(mean, offset){
  const parentMean = mean.parentid!=null?viewStateVal('means-dao', 'means')[mean.parentid]:null
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
         {draggableElem(mean.children.length==0?<span style={{'font-weight': 'bold'}}>{mean.title}</span>:mean.title,
                          parentMean, mean, ()=>fireEvent('mean-modal', 'open', [mean]))}
          <span style={{color: 'green'}}> {mean.targetsString()}</span>
        <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), mean])}>
          {addNewMeanTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {meanChildrenUI(mean.children, offset)}
        {mean.children!=null && mean.children.length==0?
          <div style={{width:'50px', height: '12px', border: '1px dotted lightgrey'}}
                onDragOver={(e)=>{e.preventDefault();fireEvent('means-dao', 'draggable-add-as-child', [mean])}}></div>
          :null}
      </div>
    </div>
  )
}

const draggableElem = function(content, parentMean, mean, onClick){
  return  <a href="#" onClick={onClick}
            draggable='true'
            onDragStart={()=>fireEvent('means-dao', 'add-draggable', [mean])}
            onDragOver={(e)=>{e.preventDefault();fireEvent('means-dao', 'replace-mean', [parentMean, mean])}}>
             {content}
           </a>
}

const meanChildrenUI = function(children, offset){
  const result = []
  if(children!=null){
    iterateLLfull(children, (mean)=>{
      result.push(<li key={'mean_'+mean.id}>
        {meanUI(mean, offset + offsetVal)}
      </li>)
    })
  }
  return result
}
