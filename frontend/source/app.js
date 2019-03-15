import React from 'react';
import ReactDOM from 'react-dom';

import {Main} from './ui/main'
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
		<div>
				<FinishingTaskModal/>
				<Main />
		</div>, app);
}
rerender();
