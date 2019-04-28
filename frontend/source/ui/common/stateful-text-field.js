import React from 'react';
import ReactDOM from 'react-dom';
import {FormControl} from 'react-bootstrap'
import {TextField} from './text-field'


//props: obj, valName, isEdit, onInput
export class StatefulTextField extends React.Component {
  constructor(props){
    super(props)
  }

  render(){
    if(this.props.isEdit){
      return <TextField obj={this.props.obj} valName={this.props.valName} onInput={this.props.onInput} />
    } else {
      const title = this.props.children!=null?this.props.children:this.props.obj[this.props.valName]
      return <FormControl.Static>{title}</FormControl.Static>
    }
  }
}
