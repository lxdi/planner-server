import React from 'react';
import ReactDOM from 'react-dom';
import {Button} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {DataConstants} from '../data/data-constants'
import {filterOutMemoTask} from '../utils/task-utils'
import {getColorForProgressStat, getPercents} from './statService'

const taskRep = 'task-rep'

export class LeftSideBarContent extends React.Component {
  constructor(props){
    super(props)

    registerReaction('actual-tasks-ui', taskRep, ['actual-tasks-rs'], ()=>this.setState({}))
    registerReaction('actual-tasks-ui', 'main-ui', ['switch-mode'], ()=>this.setState({}))

    registerReaction('actual-tasks-ui', DataConstants.progressRep, ['got-actual'], (stateSetter)=>this.setState({}))
  }

  render(){
    //return 'TODO'
    return content(this)
  }
}

const content = function(reactcomp){
  const actuals = chkSt(DataConstants.progressRep, 'actual');
  if(actuals==null){
    fireEvent(DataConstants.progressRep, 'get-actual')
    return 'Loading...'
  }
  return <div>
              <a href="#" style={{textDecoration:'none'}}>
                <div onClick={()=>fireEvent('actual-tasks-modal', 'open')} class="actual-tasks-indicators-group">
                  {getSquare(actuals.twoWeeksLate.length, 'red')}
                  {getSquare(actuals.oneWeekLate.length, 'orange')}
                  {getSquare(actuals.aboutWeekReps.length, 'green')}
                  {getSquare(actuals.upcomingReps.length, 'grey')}
                  {divisor()}
                  {getStatSquare(actuals.weekProgress, getPercents(actuals.weekProgress, 'week'), getColorForProgressStat(actuals.weekProgress, 'week'), 'Week')}
                  {getStatSquare(actuals.monthProgress, getPercents(actuals.monthProgress, 'month'), getColorForProgressStat(actuals.monthProgress, 'month'), 'Month')}
                  {getStatSquare(actuals.halfYearProgress, getPercents(actuals.halfYearProgress, 'halfYear'), getColorForProgressStat(actuals.halfYearProgress, 'halfYear'), 'Year/2')}
                  {getStatSquare(actuals.yearProgress, getPercents(actuals.yearProgress, 'year'), getColorForProgressStat(actuals.yearProgress, 'year'), 'Year')}
                </div>
              </a>
              {divisor()}
              <a href="#" style={{textDecoration:'none'}}>
                <div onClick={()=>fireEvent('priorities-modal', 'open')} class="actual-tasks-indicators-group">
                  {getSquare('P', 'blue')}
                </div>
              </a>
            </div>
}

const getSquare = function(num, color){
	return <div style={{border:'1px solid '+color, margin:'2px', width:'45px', height:'30px', borderRadius:'8px', textAlign:'center', color:color, fontSize:'13pt'}}>{num}</div>
}

const divisor = function(){
  return <div style={{backgroundColor:'lightgrey', width:'100%', height:'1px', marginLeft:'2px', marginRight:'2px', marginTop:'5px', marginBottom:'5px'}}></div>
}

const getStatSquare = function(num, percents, color, span){
  return <div style={{border:'1px solid '+color, margin:'2px', width:'45px', borderRadius:'8px', textAlign:'center', color:color, fontSize:'10pt'}}>
                  <div>{span}</div>
                  <div style = {{fontSize:'8pt'}}>{num}</div>
                  <div style = {{fontSize:'8pt'}}>{percents}%</div>
            </div>
}
