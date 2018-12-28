import React from 'react';
import ReactDOM from 'react-dom';
//import {Button, ButtonGroup, ButtonToolbar,  DropdownButton, MenuItem, ListGroup, ListGroupItem} from 'react-bootstrap'
import {iterateLLfull} from '../utils/linked-list'
import {mergeArrays, resolveNodes, replaceDraggableUtil, addAsChildDraggableUtil} from '../utils/draggable-tree-utils'

const offsetVal = 20

// props: nodes, isEdit,  viewCallback(node), onDragStartCallback(draggableNode) onDropCallback(alteredList)
// props(styles): rootStyle, groupStyle, shiftpx
// node {id, parentid, nextid}
export class TreeComponent extends React.Component {
  constructor(props){
    super(props)
    this.state = {}
    this.onDragOver = this.onDragOver.bind(this)
    this.onDrop = this.onDrop.bind(this)
  }

  onDragOver(e, node, type){
    var altered = null
    if(node!=e.draggableNode){
      if(type=='replace'){
        altered = replaceDraggableUtil(this.props.nodes, null, node, e.draggableNode, this.state.cache)
      }
      if(type=='addchild'){
        altered = addAsChildDraggableUtil(this.props.nodes, node, e.draggableNode, this.state.cache)
      }
      if(this.state.altered==null){
        this.setState({altered: altered})
      } else {
        mergeArrays(this.state.altered, altered)
        this.setState({})
      }
    }
  }

  onDrop(e){
    if(this.state.altered!=null && this.state.altered.length>0 && this.props.onDropCallback!=null){
      this.props.onDropCallback(this.state.altered)
    }
    this.setState({altered:null})
  }

  render(){
    return(
      <div>
          <div onDrop={(e)=>this.onDrop(e)} onDragOver={(e)=>e.preventDefault()}>
            {nodesView(this)}
            {this.props.isEdit!=null && this.props.isEdit? addChildDiv(this, null) :null}
          </div>
      </div>
    )
  }
}

const nodesView = function(component){
        const result = []
        component.state.cache = resolveNodes(component.props.nodes)
        iterateLLfull(component.state.cache.root, (node)=>{
          result.push(<div style={component.props.rootStyle}>
              {nodeUI(component, node, 0)}
            </div>)
        })
        return result
}

const nodeUI = function(component, node, level){
  const nextLevel = level + 1
  const shiftPx = component.props.shiftpx!=null? component.props.shiftpx*nextLevel: offsetVal*nextLevel
  return <div key={'node_'+node.id} style={component.props.groupStyle}>
          {draggableWrapper(component, node, component.props.viewCallback(node, level))}
          <div style={{paddingLeft:shiftPx}}>
            {childrenNodeUI(component, node, nextLevel)}
          </div>
        </div>
}

const draggableWrapper = function(component, node, content){
  if(component.props.isEdit!=null && component.props.isEdit){
    return <div style={{display:'inline-block'}}
                    draggable='true'
                    onDragStart={(e)=>{e.draggableNode = node; if(component.props.onDragStartCallback!=null) component.props.onDragStartCallback(node)}}
                    onDragOver={(e)=>{e.preventDefault(); component.onDragOver(e, node, 'replace')}}>
                {content}
              </div>
  } else {
    return content
  }
}

const childrenNodeUI = function(component, node, level){
  const children = component.state.cache.children[node.id]
  const childrenUI = []
  if(children!=null){
    iterateLLfull(children, (childNode)=>childrenUI.push(nodeUI(component, childNode, level)))
  } else {
    if(component.props.isEdit!=null && component.props.isEdit){
      childrenUI.push(addChildDiv(component, node))
    }
  }
  return childrenUI
}

const styleForAddAsChild = {width:'80px', color:'lightgrey', fontSize: '10px', border: '1px dotted lightgrey', borderRadius: '5px', padding: '3px'}

const addChildDiv = function(component, node){
  return <div style={styleForAddAsChild} onDragOver={(e)=>{e.preventDefault(); component.onDragOver(e, node, 'addchild')}}>
                + Add as a child
              </div>
}
