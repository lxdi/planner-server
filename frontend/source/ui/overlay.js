import React from 'react';
import ReactDOM from 'react-dom';

import {fireEvent, chkSt, registerEvent, registerReaction} from 'absevents'

export class OverlayInfo extends React.Component{
  constructor(props){
    super(props)
    this.state = {show:false, x:0, y:0}

    registerEvent('overlay-info', 'show', (stateSetter, content)=>{
      this.props.content = content
      this.setState({show:true})
    })

    registerEvent('overlay-info', 'hide', (stateSetter)=>{
      this.props.content = null
      this.setState({show:false})
    })

    registerEvent('overlay-info', 'update-pos', (stateSetter, x, y)=>{
      this.setState({x:x, y:y})
    })

    registerEvent('overlay-info', 'update', (stateSetter, x, y, content)=>{
      this.props.content = content
      this.setState({x:x, y:y, show:true})
    })

  }

  render(){
    if(this.state.show){
      return <div style = {{padding:'10px', backgroundColor:'white', position:'absolute', zIndex:'99999999', top:this.state.y, left:this.state.x, border:'2px solid black'}}>
            {this.props.content}
  				</div>
    } else {
      return null
    }
  }
}
