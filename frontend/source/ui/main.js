import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt, registerObject} from 'absevents'

import {Frame} from './frame';

export class Main extends React.Component {
	constructor(props){
		super(props);
		this.state = {}

    registerObject('main-ui', {'three-frames':true})
	}

	render() {
		return (
			<div>
				<table class='frames-table'>
									<tr>
										<td class='frame-td-means'><Frame name="Means" /></td>
										<td class='frame-td-schedule'><Frame name="Schedule" /></td>
									</tr>
								</table>
			</div>
		)
	}
}
