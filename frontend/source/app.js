import React from 'react';
import ReactDOM from 'react-dom';

import {Main} from './ui/main'
import {ActualTasks} from './ui/means/actual-tasks'
import {FinishingTaskModal} from './ui/means/finishing-task-modal'

import './data/realms-dao'
import './data/targets-dao'
import './data/means-dao'
import './data/hquarters-dao'
import './data/layers-dao'
import './data/subjects-dao'
import './data/tasks-dao'
import './data/rep-plans-dao'

ReactDOM.render(<div id="app" />, document.body);
const app = document.getElementById("app");

function rerender(){
	ReactDOM.render(
		<div style={{margin:'3px'}}>
				<FinishingTaskModal/>
				<table style={{width:'100%'}}>
					<tr>
						<td style={{width:'50px', border:'1px solid lightgrey', verticalAlign:'top'}}>
							<ActualTasks/>
						</td>
						<td>
							<Main />
						</td>
					</tr>
				</table>
		</div>, app);
}

rerender();
