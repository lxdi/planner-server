import React from 'react';
import ReactDOM from 'react-dom';

import {Main} from './ui/main'
import {LeftSideBarContent} from './ui/left-side-bar-content'
import {TaskProgressModal} from './ui/means/modal/task-progress-modal'
import {TaskFinishModal} from './ui/means/modal/task-finish-modal'
import {ActualTasksModal} from './ui/means/actual-tasks-modal'
import {TestingModal} from './ui/means/modal/testing-modal'
import {PrioritiesModal} from './ui/means/modal/priorities-modal'
import {DayModal} from './ui/schedule/day-modal'
import {ShiftPlansModal} from './ui/schedule/shift-plans-modal'
import {ExternalTaskModal} from './ui/schedule/external-task-modal'
import {AssignMeanModal} from './ui/schedule/assign-mean-modal'
import {ConfirmModal} from './ui/common/confirm-modal'
import {OverlayInfo} from './ui/overlay'

import {createRealmRep} from './data/realms-dao'
import {createMeanRep} from './data/means-dao'
import {createLayerRep} from './data/layers-dao'
import {createTaskRep} from './data/tasks-topics-testings-dao'

import './data/rep-plans-dao'
import './data/weeks-dao'
import './data/days-dao'
import './data/progress-dao'
import './data/external-tasks-dao'
import './data/drag-n-drop'

createRealmRep()
createMeanRep()
createLayerRep()
createTaskRep()

ReactDOM.render(<div id="app" />, document.body);
const app = document.getElementById("app");

function rerender(){
	ReactDOM.render(
		<div style={{margin:'3px'}}>
				{getModals()}
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

const getModals = function(){
	return <div>
						<TestingModal/>
						<TaskProgressModal/>
						<TaskFinishModal/>
						<ActualTasksModal/>
						<DayModal/>
						<ShiftPlansModal/>
						<ExternalTaskModal/>
						<AssignMeanModal/>
						<ConfirmModal/>
						<OverlayInfo />
						<PrioritiesModal />
					</div>
}

rerender();
