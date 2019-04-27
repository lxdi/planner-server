import React from 'react';
import ReactDOM from 'react-dom';
import {FormControl} from 'react-bootstrap'

//props: obj, valName
export class TextField extends React.Component{
  constructor(props){
    super(props)
  }

  render(){
    return <FormControl
        type="text"
        value={this.props.obj[this.props.valName]}
        placeholder={"Enter "+this.props.valName}
        onChange={(e)=>{this.props.obj[this.props.valName] = e.target.value; this.setState({})}}/>
  }
}
