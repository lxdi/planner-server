import React from 'react';
import ReactDOM from 'react-dom';
import {FormControl, FormGroup} from 'react-bootstrap'

//props: obj, valName
export class TextArea extends React.Component{
  constructor(props){
    super(props)
  }

  render(){
    return <FormGroup controlId="formBasicText">
                    <FormControl
                                componentClass="textarea"
                                type="input"
                                value={this.props.obj[this.props.valName]}
                                placeholder={"Enter "+this.props.valName}
                                onChange={(e)=>{this.props.obj[this.props.valName] = e.target.value; this.setState({})}}/>
                </FormGroup>
  }
}
