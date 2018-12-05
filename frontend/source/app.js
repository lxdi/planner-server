import React from 'react';
import ReactDOM from 'react-dom';
//import Modal from 'react-modal';
import {Frame} from './ui/frame';
import {SwitchButton} from './ui/changebutton';
import {state} from './state';
import {Button} from 'react-bootstrap'

import './data/realms-dao'
import './data/targets-dao'
import './data/means-dao'
import './data/hquarters-dao'
import './data/layers-dao'
import './data/subjects-dao'
import './data/tasks-dao'

ReactDOM.render(<div id="app" />, document.body);
const app = document.getElementById("app");

function rerender(){
	ReactDOM.render(<Main />, app);
}

class Main extends React.Component {
	constructor(props){
		super(props);
		this.state = {}
		this.switchMode = this.switchMode.bind(this);
	}

	switchMode(){
		state.threeFrames = !state.threeFrames
		this.setState({})
	}

	render() {
		var leftFrame = null;
		var rightFrame = null;
		var thirdFrame = null;
		var framesTable = null;
		var switchButton = <div></div>;
		if(!state.threeFrames){
			if(state.currState == "Targets"){
				leftFrame = <Frame name="Targets" />
				rightFrame = <Frame name="Means" />
			} else {
				leftFrame = <Frame name="Means" />
				rightFrame = <Frame name="Schedule" />
			}
			switchButton = <div class='switch-button-div'>
												{<SwitchButton rerender={rerender} />}
										</div>
			framesTable = <table class='frames-table'>
								<tr>
									<td class='frame-td'>{leftFrame}</td>
									<td class='frame-td'>{rightFrame}</td>
								</tr>
							</table>
		} else {
			leftFrame = <Frame name="Targets" />
			rightFrame = <Frame name="Means" />
			thirdFrame = <Frame name="Schedule" />
			framesTable = <table class='frames-table'>
									<tr>
										<td class='frame-td-3f'>{leftFrame}</td>
										<td class='frame-td-3f'>{rightFrame}</td>
										<td class='frame-td-3f'>{thirdFrame}</td>
									</tr>
								</table>
		}
		return (
			<div>
				<div align = "center" >
					{switchButton}
					<div align='right' class='mode-button-div'>
						<Button onClick = {this.switchMode}>Mode</Button>
					</div>
				</div>
				{framesTable}
			</div>
		)
	}
}
rerender();
