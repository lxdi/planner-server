import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from '../../data/data-constants'
import {BidirectList} from '../components/bidirect-list'
import {WeekElement} from './week-frame-element'

const fakeWeek = {
  days: [{weekDay:'mon'}, {weekDay:'tue'}, {weekDay:'wed'}, {weekDay:'thu'}, {weekDay:'fri'}, {weekDay:'sat'}, {weekDay:'sun'}]
}

export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {}

    registerReaction('shedule-frame', DataConstants.weekRep, ['got-current-list', 'got-next', 'got-prev', 'moved-plans'], (stSetter)=>{
      this.setState({weeks: chkSt(DataConstants.weekRep, DataConstants.objList)})
    })

    registerReaction('shedule-frame', DataConstants.progressRep, ['got-by-task'], ()=>{
      fireEvent(DataConstants.weekRep, 'clean-all')
      this.setState({weeks: null})
    })
  }

  render(){
    return <div style={{height:'85vh', borderTop:'1px solid lightgrey', borderBottom:'1px solid lightgrey', marginTop:'3px'}}>
              <div style={{paddingRight: '14px'}}> <WeekElement week = {fakeWeek} full = {false} /> </div>
              {getContent(this)}
            </div>
  }
}


const getContent = function(component){
  if(component.state.weeks == null){
    fireEvent(DataConstants.weekRep, 'get-current-list')
    return 'Loading...'
  }

  const weeksMap = chkSt(DataConstants.weekRep, DataConstants.objMap)

  return <BidirectList firstNode={chkSt(DataConstants.weekRep, DataConstants.objList)[0]}
                        getNext = {(week, isExtend)=>getNextHandler(weeksMap, week, isExtend)}
                        getPrev = {(week)=>getPrevHandler(weeksMap, week)}
                        nodeView = {(week)=><WeekElement week = {week}/>}
                        loadPrev={true}
                        />
}

const getNextHandler = function(weeksMap, week, isExtend){
  if(week == null){
    return null
  }

  const nextWeek = weeksMap[week.nextid]
  if(nextWeek == null && isExtend){
    fireEvent(DataConstants.weekRep, 'get-next', [week])
    return null
  }

  return nextWeek
}

const getPrevHandler = function(weeksMap, week){
  if(week == null){
    return null
  }

  const prevWeek = weeksMap[week.previd]
  if(prevWeek == null){
    fireEvent(DataConstants.weekRep, 'get-prev', [week])
    return null
  }

  return prevWeek
}
