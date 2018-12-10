import {createNewMeanButtonTitle, addNewMeanTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {CreateMean} from './../../data/creators'
import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {MeanModal} from './mean-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'
import {sortByField} from '../../utils/import-utils'
import {iterateLLfull} from '../../utils/linked-list'
import {mergeArrays, resolveNodes, replaceDraggableUtil, addAsChildDraggableUtil} from '../../utils/draggable-tree-utils'

const offsetVal = 10

export class MeansFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {isEdit: false}
    this.onDragOver = this.onDragOver.bind(this)
    this.onDrop = this.onDrop.bind(this)

    var uiUpdate = function(){
      this.setState({})
    }.bind(this)

    registerReaction('means-frame', 'targets-dao', 'target-deleted', (state, targetid)=>{
      fireEvent('means-dao', 'delete-depended-means', [targetid])
      this.setState({})
    })

    registerReaction('means-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
    registerReaction('means-frame', 'means-dao',
            ['means-received', 'replace-mean', 'mean-created', 'mean-deleted', 'mean-modified', 'means-list-modified', 'draggable-add-as-child'], ()=>this.setState({}))
  }

  onDragOver(mean, parentMean, type){
    var altered = null
    if(type=='replace'){
      altered = replaceDraggableUtil(this.state.allNodes, parentMean, mean, this.state.draggableMean, this.state.cache)
    }
    if(type=='addchild'){
      altered = addAsChildDraggableUtil(this.state.allNodes, mean, this.state.draggableMean, this.state.cache)
    }
    if(this.state.altered==null){
      this.setState({altered: altered})
    } else {
      mergeArrays(this.state.altered, altered)
      this.setState({})
    }
  }

  onDrop(){
    if(this.state.altered!=null && this.state.altered.length>0){
      fireEvent('means-dao', 'modify-list', [this.state.altered])
    }
    fireEvent('means-dao', 'remove-draggable', [])
    this.setState({altered:null, draggableMean:null})
    //this.state.altered=null
  }

  // <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, [])])}>
  //   {createNewMeanButtonTitle}
  // </Button>

  render(){
    return(
      <div>
        {viewStateVal('realms-dao', 'currentRealm')!=null?
          <div style={{'margin-bottom': '3px'}}>
            {getControlButtons(this)}
            <MeanModal/>
          </div>:null}
          <div onDrop={this.onDrop} onDragOver={(e)=>e.preventDefault()}>
            <ListGroup style={{marginBottom: '3px'}}>
              {meansUIlist(this)}
            </ListGroup>
            {this.state.isEdit? <div style={styleForAddAsChild} onDragOver={(e)=>{e.preventDefault(); this.onDragOver(null, null, 'addchild')}}>
              + Add as a child
            </div> :null}
          </div>
      </div>
    )
  }
}

const getControlButtons = function(component){
  const result = []
  result.push(<Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, [])])}>
                {createNewMeanButtonTitle}
              </Button>)
  result.push(<Button bsStyle="default" bsSize="xsmall" onClick={()=>component.setState({isEdit: !component.state.isEdit})}>
                  {component.state.isEdit? 'View': 'Edit'}
                </Button>)
  return <ButtonGroup>{result}</ButtonGroup>
}

const styleForAddAsChild = {width:'80px', color:'lightgrey', fontSize: '10px', border: '1px dotted lightgrey', borderRadius: '5px', padding: '3px'}

const meansUIlist = function(component){
    if(viewStateVal('means-dao', 'means')!=null){
      const result = []
      if(viewStateVal('realms-dao', 'currentRealm')!=null){
        component.state.allNodes = viewStateVal('means-dao', 'means')[viewStateVal('realms-dao', 'currentRealm').id]
        component.state.cache = resolveNodes(component.state.allNodes)
        iterateLLfull(component.state.cache.root, (mean)=>{
          result.push(<ListGroupItem key={'mean_'+mean.id}>{meanUI(component, mean, 20)}</ListGroupItem>)
        })
      }
      return result
    } else {
      return "Loading..."
    }
}

const meanUI = function(component, mean, offset){
  const parentMean = mean.parentid!=null? component.state.allNodes[mean.parentid]:null
  return (
    <div>
      <div style={{'margin-bottom': '5px'}}>
         {draggableElem(component,
             countFields(component.state.cache.children[mean.id])==0?<span style={{'font-weight': 'bold'}}>{mean.title}</span>:mean.title,
             parentMean, mean, ()=>fireEvent('mean-modal', 'open', [mean]))}
          <span style={{color: 'green'}}> {mean.targetsString()}</span>
        <a href="#" onClick={()=>fireEvent('mean-modal', 'open', [CreateMean(0, '', viewStateVal('realms-dao', 'currentRealm').id, []), mean])}>
          {addNewMeanTitle}
        </a>
      </div>
      <div style={{'margin-left': offset + 'px'}}>
        {meanChildrenUI(component, component.state.cache.children[mean.id], offset)}
        {component.state.isEdit && countFields(component.state.cache.children[mean.id])==0?
          <div style={styleForAddAsChild}
                onDragOver={(e)=>{e.preventDefault(); component.onDragOver(mean, null, 'addchild')}}>+ Add as a child</div>
          :null}
      </div>
    </div>
  )
}

const draggableElem = function(component, content, parentMean, mean, onClick){
  if(component.state.isEdit){
    return  <a href="#" onClick={onClick}
              draggable='true'
              onDragStart={()=>{component.state.draggableMean = mean; fireEvent('means-dao', 'add-draggable', [mean])}}
              onDragOver={(e)=>{e.preventDefault(); component.onDragOver(mean, parentMean, 'replace')}}>
               {content}
             </a>
  } else {
    return  <a href="#" onClick={onClick} draggable='true' onDragStart={()=>{fireEvent('means-dao', 'add-draggable', [mean])}}>
              {content}
            </a>
  }
}

const meanChildrenUI = function(component, children, offset){
  const result = []
  if(children!=null){
    iterateLLfull(children, (mean)=>{
      result.push(<li key={'mean_'+mean.id}>
        {meanUI(component, mean, offset + offsetVal)}
      </li>)
    })
  }
  return result
}

const countFields = (obj)=>{
  var result = 0;
  if(obj!=null){
    for(var i in obj){result++};
  }
  return result
}
