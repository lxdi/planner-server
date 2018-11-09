import {switchButtonTitle} from './../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {state} from './../state'

export class SwitchButton extends React.Component {
	constructor(props) {
		super(props);
		// This binding is necessary to make `this` work in the callback
		this.buttonHandler = this.buttonHandler.bind(this);
	}

	buttonHandler(){
		if(state.currState == "Schedule"){
      state.currState = "Targets"
			this.props.rerender();
		} else {
      state.currState = "Schedule";
			this.props.rerender();
		}
	}

	render() {
		return (
			<Button bsStyle="primary" onClick={this.buttonHandler}>
				{switchButtonTitle}
			</Button>
		);
	}
}
