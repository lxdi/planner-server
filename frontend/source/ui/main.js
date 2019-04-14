import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'

import {registerEvent, registerReaction, fireEvent, chkSt, registerObject} from 'absevents'

import {Frame} from './frame';
//import {SwitchButton} from './changebutton';

export class Main extends React.Component {
	constructor(props){
		super(props);
		this.state = {}

    registerObject('main-ui', {'three-frames':true, currState:'Targets'})

    registerEvent('main-ui', 'switch-mode', (stateSetter)=>{
      stateSetter('three-frames', !chkSt('main-ui', 'three-frames'))
      this.setState({})
    })

    registerEvent('main-ui', 'switch-curr-state', (stateSetter)=>{
      if(chkSt('main-ui', 'currState')=='Targets'){
        stateSetter('currState', 'Schedule')
      } else {
        stateSetter('currState', 'Targets')
      }
      this.setState({})
    })
	}

	render() {
		return (
			<div>
				{getFramesTable()}
			</div>
		)
	}
}

const getFramesTable = function(){
  if(!chkSt('main-ui', 'three-frames')){
    var leftFrame = null
    var rightFrame = null
    if(chkSt('main-ui', 'currState') == "Targets"){
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
  if(!chkSt('main-ui', 'three-frames')){
    return <div class='switch-button-div'>
                      <Button bsStyle="primary" onClick={()=>fireEvent('main-ui', 'switch-curr-state')}>Switch</Button>
                  </div>
  } else {
    return <div></div>
  }
}
