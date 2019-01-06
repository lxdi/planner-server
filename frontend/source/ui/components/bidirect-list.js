import React from 'react';
import ReactDOM from 'react-dom';

//props(required): firstNode(id), getPrev(node), getNext(node), nodeView(node)
//props (not required): loadPrev(bool) loadNext(bool) lastNode(id), compareNodes(node1, node2)
export class BidirectList extends React.Component {
  constructor(props){
    super(props)
    this.state = {}
    this.state.node = this.props.node
    this.state.firstNode = this.props.firstNode
    this.state.lastNode = this.props.lastNode
  }

  shouldComponentUpdate(nextProps, nextState){
    this.state.firstNode = nextProps.firstNode
    this.state.lastNode = nextProps.lastNode
    return true
  }

  render(){
    const elems = []
    fillRange(this, elems, this.state.firstNode, this.state.lastNode)
    return <div style={{overflow:'auto', height:'100%'}}
                onWheel={(e)=>onWheelHandler(e, this)} onScroll={(e)=>onScrollHandler(e)}>
                {elems}
            </div>
  }
}

const onWheelHandler = function(e, component){
  if(e.deltaY>0 && (component.props.loadNext==null || component.props.loadNext==true)){
    if(isBarOnTheBottom(e.currentTarget)){
      const nextLastNode = component.props.getNext(component.state.lastNode, true)
      if(nextLastNode!=null){
          component.state.lastNode = nextLastNode
      }
    }
  }
  if(e.deltaY<0 && (component.props.loadPrev==null || component.props.loadPrev==true) ){
    if(e.currentTarget.scrollTop==0){
      const nextFirstNode = component.props.getPrev(component.state.firstNode)
      if(nextFirstNode!=null){
          component.state.firstNode = nextFirstNode
      }
    }
  }
  // if(e.deltaY>0 && e.currentTarget.scrollTop==0){
  //   e.preventDefault()
  //   e.currentTarget.scrollTop = 1
  // }
  component.setState({})
}

const isBarOnTheBottom = function(target){
  return (target.clientHeight+target.scrollTop)>=target.scrollHeight
}

const onScrollHandler = function(e){
  //console.log(e)
}

const fillRange = function(component, elems, startNode, stopNode){
  elems.push(component.props.nodeView(startNode))

  var currnode = component.props.getNext(startNode, false)
  while(currnode!=null){
    if(stopNode==null || !compareNodes(component, currnode, stopNode)){
      elems.push(component.props.nodeView(currnode))
      const prevnode = currnode
      currnode = component.props.getNext(currnode, false)
      if(stopNode==null && currnode==null){
        component.state.lastNode = prevnode
      }
    } else {
      break;
    }
  }
  if(stopNode!=null){
    elems.push(component.props.nodeView(stopNode))
  }
}

const compareNodes = function(component, node1, node2){
  return component.props.compareNodes!=null? component.props.compareNodes(node1, node2): node1==node2
}
