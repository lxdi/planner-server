import React from 'react';
import ReactDOM from 'react-dom';

import {Main} from './ui/main'
import {LeftSideBarContent} from './ui/left-side-bar-content'
import {TaskProgressModal} from './ui/means/modal/task-progress-modal'
import {TaskFinishModal} from './ui/means/modal/task-finish-modal'
import {ActualTasksModal} from './ui/means/actual-tasks-modal'
import {OverlayInfo} from './ui/overlay'

import {createRealmRep} from './data/realms-dao'
import {createTargetRep} from './data/targets-dao'
import {createMeanRep} from './data/means-dao'
import {createLayerRep} from './data/layers-dao'
import {createTaskRep} from './data/tasks-topics-testings-dao'

import './data/rep-plans-dao'
import './data/weeks-dao'
import './data/progress-dao'

createRealmRep()
createTargetRep()
createMeanRep()
createLayerRep()
createTaskRep()

ReactDOM.render(<div id="app" />, document.body);
const app = document.getElementById("app");

function rerender(){
	ReactDOM.render(
		<div style={{margin:'3px'}}>
				<TaskProgressModal/>
				<TaskFinishModal/>
				<ActualTasksModal/>
				<OverlayInfo />
				<table style={{width:'100%'}}>
					<tr>
						<td style={{width:'50px', verticalAlign:'top'}}>
							<LeftSideBarContent/>
						</td>
						<td>
							<Main />
						</td>
					</tr>
				</table>
		</div>, app);
}

rerender();
