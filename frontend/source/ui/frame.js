import React from 'react';
import ReactDOM from 'react-dom';
import {TargetsFrame} from './targets/targets-frame';
import {MeansFrame} from './means/means-frame';
import {ScheduleFrame} from './schedule/schedule-frame';

export class Frame extends React.Component {
  render(){
    var frameContent;
    if(this.props.name=="Targets")
      frameContent = <TargetsFrame />;
    if(this.props.name=="Schedule")
      frameContent = <div style={{height:'85vh', borderTop:'1px solid lightgrey', borderBottom:'1px solid lightgrey', marginTop:'3px'}}>
                TODO
              </div>
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
