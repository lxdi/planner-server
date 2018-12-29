import React from 'react';
import ReactDOM from 'react-dom';
import {iterateLL, iterateLLReverse} from '../utils/linked-list'

const compHeight = 300
const elemHeight = 30
const shift = 10

//props : node{id}, getPrev(node), getNext(node)
export class BidirectList extends React.Component {
  constructor(props){
    super(props)
    this.state = {}
    this.state.node = this.props.node
    this.state.topShift = compHeight/2
  }

  render(){
    const elems = []
    fillPrev(this, elems, this.state.node)
    if(this.state.topShift>0 && this.state.topShift+elemHeight<compHeight){
      elems.push(wrappNodeUI(this, this.state.node, this.state.topShift))
    }
    fillNext(this, elems, this.state.node)

    return <div style={{border:'1px solid black', height:compHeight+'px', position:'relative'}}
                onWheel={(e)=>scrollAction(e, this)}>
                {elems}
            </div>
  }
}

const scrollAction = function(e, component){
  if(e.deltaY<0){ //up
    const newTopShift = component.state.topShift-shift
    if(newTopShift>0){
      component.state.topShift = newTopShift
    } else {
      const nextNode = component.props.getNext(component.state.node)
      if(nextNode!=null){
        component.state.node = nextNode
      }
    }
    //component.state.topShift = component.state.topShift+shift
  }
  if(e.deltaY>0){//down
    const newTopShift = component.state.topShift+shift
    if(newTopShift<compHeight){
      component.state.topShift = newTopShift
    } else {
      const prevNode = component.props.getPrev(component.state.node)
      if(prevNode!=null){
        component.state.node = prevNode
      }
    }
    //component.state.topShift = component.state.topShift-shift
  }
  e.preventDefault()
  component.setState({})
}

const fillPrev = function(component, elems, startNode){
  var count = 1
  var currnode = component.props.getPrev(startNode)
  while(currnode!=null){
    const position = component.state.topShift-count*elemHeight
    if(position>0 && position+elemHeight<compHeight){
      elems.unshift(wrappNodeUI(component, currnode, position))
      currnode = component.props.getPrev(currnode)
      count++
    } else {
      break;
    }
  }
}

const fillNext = function(component, elems, startNode){
  var count = 1
  var currnode = component.props.getNext(startNode)
  while(currnode!=null){
    const position = component.state.topShift+count*elemHeight
    if(position>0 && position+elemHeight<compHeight){
      elems.push(wrappNodeUI(component, currnode, position))
      currnode = component.props.getNext(currnode)
      count++
    } else {
      break;
    }
  }
}

const wrappNodeUI = function(component, node, position){
  return <div style={{position:'absolute', width:'100%', height:elemHeight-3+'px', border:'1px solid red', top:position+'px', marginBottom:'3px'}}>
            Node with id {node.id}
          </div>
}
