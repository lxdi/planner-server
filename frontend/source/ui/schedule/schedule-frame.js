import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from '../../data/data-constants'
import {BidirectList} from '../components/bidirect-list'
import {WeekElement} from './week-frame-element'

export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}
    registerReaction('shedule-frame', DataConstants.weekRep, 'got-current-list', (stSetter, list)=>{
      this.setState({weeks: list})
    })

  }

  render(){
    return <div style={{height:'85vh', borderTop:'1px solid lightgrey', borderBottom:'1px solid lightgrey', marginTop:'3px'}}>
              {getContent(this)}
            </div>
  }
}


const getContent = function(component){
  if(component.state.weeks == null){
    fireEvent(DataConstants.weekRep, 'get-current-list')
    return 'Loading...'
  }

  // const result = []
  // component.state.weeks.forEach(week => {
  //   result.push(<WeekElement week = {week}/>)
  // })
  // return result

  return <BidirectList firstNode={chkSt(DataConstants.weekRep, DataConstants.objList)[0]}
                        getNext = {(week, isExtend)=>getNextHandler(week)}
                        getPrev = {(week)=>getPrevHandler(week)}
                        nodeView = {(week)=><WeekElement week = {week}/>}
                        loadPrev={true}
                        />
}

const getNextHandler = function(week){
  if(week == null){
    return null
  }
  return chkSt(DataConstants.weekRep, DataConstants.objMap)[week.nextid]
}

const getPrevHandler = function(week){
  if(week == null){
    return null
  }
  return chkSt(DataConstants.weekRep, DataConstants.objMap)[week.previd]
}
