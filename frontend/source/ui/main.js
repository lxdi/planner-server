import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'

import {registerEvent, registerReaction, fireEvent, viewStateVal, registerObject} from '../controllers/eventor'

import {Frame} from './frame';
//import {SwitchButton} from './changebutton';

export class Main extends React.Component {
	constructor(props){
		super(props);
		this.state = {}

    registerObject('main-ui', {'three-frames':true, currState:'Targets'})

    registerEvent('main-ui', 'switch-mode', (stateSetter)=>{
      stateSetter('three-frames', !viewStateVal('main-ui', 'three-frames'))
      this.setState({})
    })

    registerEvent('main-ui', 'switch-curr-state', (stateSetter)=>{
      if(viewStateVal('main-ui', 'currState')=='Targets'){
        stateSetter('currState', 'Schedule')
      } else {
        stateSetter('currState', 'Targets')
      }
      this.setState({})
    })
	}

	render() {
		return (
			<div style={{border:"1px solid lightgrey"}}>
				{getFramesTable()}
			</div>
		)
	}
}

// <div align = "center" >
// 	{getSwitchButton()}
// 	<div align='right' class='mode-button-div'>
// 		<Button onClick = {()=>fireEvent('main-ui', 'switch-mode')}>Mode</Button>
// 	</div>
// </div>


// <div style = {{height:'20px', border:'1px solid red'}}>
//
// </div>

const getFramesTable = function(){
  if(!viewStateVal('main-ui', 'three-frames')){
    var leftFrame = null
    var rightFrame = null
    if(viewStateVal('main-ui', 'currState') == "Targets"){
      leftFrame = <Frame name="Targets" />
      rightFrame = <Frame name="Means" />
    } else {
      leftFrame = <Frame name="Means" />
      rightFrame = <Frame name="Schedule" />
    }
    return <table class='frames-table'>
              <tr>
                <td class='frame-td'>{leftFrame}</td>
                <td class='frame-td'>{rightFrame}</td>
              </tr>
            </table>
  } else {
    return <table class='frames-table'>
                <tr>
                  <td class='frame-td-3f'><Frame name="Targets" /></td>
                  <td class='frame-td-3f'><Frame name="Means" /></td>
                  <td class='frame-td-3f'><Frame name="Schedule" /></td>
                </tr>
              </table>
  }
}

const getSwitchButton = function(){
  if(!viewStateVal('main-ui', 'three-frames')){
    return <div class='switch-button-div'>
                      <Button bsStyle="primary" onClick={()=>fireEvent('main-ui', 'switch-curr-state')}>Switch</Button>
                  </div>
  } else {
    return <div></div>
  }
}
