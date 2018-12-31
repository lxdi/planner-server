import React from 'react';
import ReactDOM from 'react-dom';

//props(required): firstNode(id), getPrev(node), getNext(node), nodeView(node)
//props (not required): lastNode(id), compareNodes(node1, node2)
export class BidirectList extends React.Component {
  constructor(props){
    super(props)
    this.state = {}
    this.state.node = this.props.node
    this.state.firstNode = this.props.firstNode
    this.state.lastNode = this.props.lastNode
  }

  render(){
    const elems = []
    fillRange(this, elems, this.state.firstNode, this.state.lastNode)
    return <div style={{position:'relative', overflow:'auto', height:'100%'}}
                onWheel={(e)=>onWheelHandler(e, this)} onScroll={(e)=>onScrollHandler(e)}>
                {elems}
            </div>
  }
}

const onWheelHandler = function(e, component){
  if(e.deltaY>0){
    if(component.state.lastNode!=null){
      const nextLastNode = component.props.getNext(component.state.lastNode)
      if(nextLastNode!=null){
          component.state.lastNode = nextLastNode
      }
    }
  }
  if(e.deltaY<0){
    const nextFirstNode = component.props.getPrev(component.state.firstNode)
    if(nextFirstNode!=null){
        component.state.firstNode = nextFirstNode
    }
  }
  if(e.deltaY>0 && e.currentTarget.scrollTop==0){
    e.preventDefault()
    e.currentTarget.scrollTop = 1
  }
  component.setState({})
}

const onScrollHandler = function(e){
  //console.log(e)
}

const fillRange = function(component, elems, startNode, stopNode){
  elems.push(component.props.nodeView(startNode))

  var currnode = component.props.getNext(startNode)
  while(currnode!=null){
    if(stopNode==null || !compareNodes(component, currnode, stopNode)){
      elems.push(component.props.nodeView(currnode))
      currnode = component.props.getNext(currnode)
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
