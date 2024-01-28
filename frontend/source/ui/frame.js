import React from 'react';
import ReactDOM from 'react-dom';
import {MeansFrame} from './means/means-frame';
import {ScheduleFrame} from './schedule/schedule-frame';

export class Frame extends React.Component {
  render(){
    var frameContent;
    if(this.props.name=="Schedule")
      frameContent = <ScheduleFrame />
    if(this.props.name=="Means")
      frameContent = <MeansFrame />

    //style={{overflow:'hidden'}}>
    return (
    <div class="frame" id = {this.props.name}>
      <p class = "frame_title"><h4>{this.props.name}</h4></p>
      <div class = "frame_content">
        {frameContent}
      </div>
    </div>
    );
  }
}
