import React from 'react';
import ReactDOM from 'react-dom';
//import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {iterateLLfull} from '../utils/linked-list'
import {mergeArrays, resolveNodes, replaceDraggableUtil, addAsChildDraggableUtil} from '../utils/draggable-tree-utils'

const offsetVal = 10

//props: nodes, viewCallback, changeTreeCallback
// node {id, parentid, nextid}
export class TreeComponent extends React.Component {
  constructor(props){
    super(props)
    this.state = {isEdit: false}
    this.onDragOver = this.onDragOver.bind(this)
    this.onDrop = this.onDrop.bind(this)
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
      //fireEvent('means-dao', 'modify-list', [this.state.altered])
    }
    //fireEvent('means-dao', 'remove-draggable', [])
    this.setState({altered:null, draggableMean:null})
    //this.state.altered=null
  }

  render(){
    return(
      <div>
          <div onDrop={this.onDrop} onDragOver={(e)=>e.preventDefault()}>
            {nodesView(this)}
            {this.state.isEdit? <div style={styleForAddAsChild} onDragOver={(e)=>{e.preventDefault(); this.onDragOver(null, null, 'addchild')}}>
              + Add as a child
            </div> :null}
          </div>
      </div>
    )
  }
}

const styleForAddAsChild = {width:'80px', color:'lightgrey', fontSize: '10px', border: '1px dotted lightgrey', borderRadius: '5px', padding: '3px'}

const nodesView = function(component){
        const result = []
        component.state.cache = resolveNodes(component.props.nodes)
        iterateLLfull(component.state.cache.root, (node)=>{
          result.push(<div style={component.props.rootStyle}>
              {nodeUI(component, node, 20)}
            </div>)
        })
        return result
}

const nodeUI = function(component, node, shift){
  const children = component.state.cache.children[node.id]
  const childrenUI = []
  if(children!=null){
    iterateLLfull(children, (childNode)=>{
      childrenUI.push(nodeUI(component, childNode, shift+offsetVal))
    })
  }
  return <div key={'node_'+node.id} style={component.props.groupStyle}>
          {component.props.viewCallback(node)}
          <div style={{paddingLeft:shift}}>
            {childrenUI}
          </div>
        </div>
}
