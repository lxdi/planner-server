import React from 'react';
import ReactDOM from 'react-dom';
import {FormControl, FormGroup} from 'react-bootstrap'

//props: obj, valName, valNameUI, readOnly
export class TextArea extends React.Component{
  constructor(props){
    super(props)
  }

  render(){
    return <FormGroup controlId="formBasicText">
                    <FormControl
                                readOnly={this.props.readOnly!=null?this.props.readOnly:false}
                                componentClass="textarea"
                                type="input"
                                value={this.props.obj[this.props.valName]}
                                placeholder={"Enter "+(this.props.valNameUI!=null?this.props.valNameUI:this.props.valName)}
                                onChange={(e)=>{this.props.obj[this.props.valName] = e.target.value; this.setState({})}}/>
                </FormGroup>
  }
}
