import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table, ButtonGroup} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {HquarterModal} from './hquarter-modal'
import {BigMapModal} from './big-map-modal'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {BidirectList} from '../components/bidirect-list'
import {formatDate} from '../../utils/date-utils'

const safeSPcount = 18

export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {edit:false};
    registerEvent('schedule-frame', 'switch-edit-mode', (stateSetter)=>this.setState({edit:!this.state.edit}))
    registerEvent('schedule-frame', 'update', (stateSetter)=>this.setState({}))

    registerReaction('schedule-frame', 'hquarters-dao', ['hquarters-received', 'hquarter-modified', 'default-received', 'shift-completed'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'means-dao', ['means-received', 'mean-modified'], ()=>this.setState({}))
    //registerReaction('schedule-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
  }

  allowDrop(ev) {
    ev.preventDefault();
  }

  render(){
    return(
      <div>
        <HquarterModal/>
        <BigMapModal/>
        <ButtonGroup>
          <Button bsStyle="primary" bsSize="xsmall" onClick={()=>fireEvent('hquarter-modal', 'open', [chkSt('hquarters-dao', 'default')])}>Default settings</Button>
          <Button bsStyle="default" bsSize="xsmall" onClick={()=>fireEvent('big-map-modal', 'open')}>Big map</Button>
          <Button bsStyle="default" bsSize="xsmall" onClick={()=>fireEvent('schedule-frame', 'switch-edit-mode')}>{this.state.edit?'View': 'Edit'}</Button>
          {this.state.edit?<Button bsStyle="default" bsSize="xsmall" onClick={()=>loadPrevNextManually(this, 'prev')}>Load prev</Button>:null}
          {this.state.edit?<Button bsStyle="default" bsSize="xsmall" onClick={()=>loadPrevNextManually(this, 'next')}>Load next</Button>:null}
        </ButtonGroup>
        <div style={{height:'85vh', borderTop:'1px solid lightgrey', borderBottom:'1px solid lightgrey', marginTop:'3px'}}>
          {chkSt('means-dao', 'means')!=null?hquartersUI(this):null}
        </div>
      </div>
    )
  }
}

const loadPrevNextManually = function(component, type){
  if(type=='prev'){
    loadPrev(chkSt('hquarters-dao', 'hquarters')[chkSt('hquarters-dao', 'firstHquarterId')])
  }
  if(type=='next'){
    loadNext(null)
  }
}

const hquartersUI = function(component){
  if(chkSt('hquarters-dao', 'hquarters') != null){
    return <BidirectList firstNode={chkSt('hquarters-dao', 'hquarters')[chkSt('hquarters-dao', 'firstHquarterId')]}
                          getNext = {(node, isExtend)=>getNextHandler(component, node, isExtend)}
                          getPrev = {(node)=>getPrevHandler(component, node)}
                          nodeView = {(node)=>hquarterUI(component, node)}
                          loadPrev={component.state.edit}
    />
  } else {
    fireEvent('hquarters-dao', 'hquarters-request')
    return 'Loading...'
  }
}

const getPrevHandler = function(component, node){
  if(node.id=='loading'){
    return null
  }
  const prevNode = chkSt('hquarters-dao', 'hquarters')[node.previd]
  if(prevNode==null){
    return loadPrev(node)
  }
  return prevNode
}

const loadPrev = function(node){
  fireEvent('hquarters-dao', 'hquarters-prev', [node])
  return {id:'loading', nextid:Date.parse(node.startWeek.startDay)}
}

const getNextHandler = function(component, node, isExtend){
  const nextNode = chkSt('hquarters-dao', 'hquarters')[node.nextid]
  if(isExtend && nextNode==null){
    return loadNext(node)
  }
  return nextNode
}

const loadNext = function(node){
  fireEvent('hquarters-dao', 'hquarters-next', [node])
  return {id:'loading', previd:node!=null?Date.parse(node.startWeek.startDay):null}
}

const hquarterUI = function(component, hquarter){
  if(hquarter.id=='loading'){
    return <div key = {hquarter.id}>Loading ...</div>
  }
  return <div style={hquarter.startWeek.number<=4?{borderTop: '3px solid darkgrey', paddingTop:'3px'}:null}>
      <div key = {hquarter.id} style={Object.assign({marginBottom:'5px'}, getHquarterLayoutStyle(hquarter))}>
            <Table striped bordered condensed hover width={'100px'} key={"hquarter_"+weekToString(hquarter.startWeek)} >
              <tbody>
                <tr>
                  <td>
                    {hquarterHeaderUI(component, hquarter)}
                  </td>
                </tr>
                {getSlotsUI(component, hquarter)}
              </tbody>
            </Table>
          </div>
        </div>
}

const hquarterHeaderUI = function(component, hquarter){
  return              <div style={{width:'100%'}}>
                        <div style={{float:'left'}}>
                          <a href='#' onClick={()=>fireEvent('hquarter-modal', 'open', [hquarter])} style={isCurrentHquarter(hquarter)?{fontWeight: 'bold'}:{}}>
                            W-{hquarter.startWeek.number}: {formatDate(hquarter.startWeek.startDay)} to {formatDate(hquarter.endWeek.endDay)} {repsCountUI(hquarter)}
                          </a>
                        </div>
                        <div  style={{float:'right'}}>
                          {component.state.edit && !isCurrentOrPrevHquarter(hquarter)?
                            <a href='#' onClick={()=>fireEvent('hquarters-dao', 'shift', [hquarter])}>shift</a>
                            :null}
                        </div>
                      </div>
}

const weekToString = function(week){
  return week!=null? week.startDay: "default week (TODO - remove!)"
}

const repsCountUI = function(hquarter){
  return <span>(SP:<span style={{color:hquarter.repsCount>safeSPcount?'red':'green'}}>{hquarter.repsCount}</span>/{hquarter.repsOnlyCount})</span>
}

const getSlotsUI = function(component, hquarter){
  const result = []
  for(var slotpos in hquarter.slotsLazy){
    const slot = hquarter.slotsLazy[slotpos]
    result.push(getSlotView(component, slot))
  }
  return result
}

const getSlotView = function(component, slot){
  if(slot.meanid!=null){
    const mean = findMean(slot.meanid)
    //const realm = chkSt('realms-dao', 'realms')[mean.realmid]
    return <tr key={slot.id}>
                    <td>
                      <a href='#' style={getStyleFroSlot(slot)}>{getSlotTitleWithMean(slot.meanid)}</a>
                      {component.state.edit? <a href='#' onClick={()=>fireEvent('hquarters-dao', 'unassign-mean', [slot])}> X </a>:null}
                    </td>
                  </tr>
  } else {
    return <tr key={slot.id}
                  onDragOver={(e)=>{e.preventDefault()}}
                  onDrop={(e)=>fireEvent('hquarters-dao', 'assign-mean-to-slot', [chkSt('means-dao', 'draggableMean'), slot])}>
                    <td>
                        <span style={{color:'lightgrey'}}>Slot {slot.position}</span>
                        {assignMeanLink(component, slot)}
                    </td>
                  </tr>
  }
}

const assignMeanLink = function(component, slot){
  const draggableMean = chkSt('means-dao', 'draggableMean')
  if(component.state.edit && draggableMean!=null){
    return <a href='#' onClick={(e)=>{
      fireEvent('hquarters-dao', 'assign-mean-to-slot', [chkSt('means-dao', 'draggableMean'), slot])
    }}> Assign {draggableMean.title}</a>
  }
}

const getStyleFroSlot = function(slot){
  if(slot.tasksInLayer == null){
    return {color: 'darkgrey'}
  }
  if(slot.tasksInLayer < 9){
    return {color: 'orange'}
  }
  if(slot.tasksInLayer == 9){
    return {color: 'LimeGreen'}
  }
  if(slot.tasksInLayer > 9){
    return {color: 'red'}
  }
}

const getHquarterLayoutStyle = function(hquarter){
  var result = {border:'1px solid LightGreen', borderRadius:'5px'}
  if(isCurrentOrPrevHquarter(hquarter)){
    result = Object.assign(result, {border:'1px solid LightSalmon '})
  }
  if(isCurrentHquarter(hquarter)){
    result = Object.assign(result, {border:'1px solid gold'})
  }
  return result
}

const getSlotTitleWithMean = function(meanid){
  const mean = findMean(meanid)
  const realm = chkSt('realm-rep', 'objects')[mean.realmid]
  var chain = []
  var currMean = mean
  while(currMean!=null){
    chain.unshift(currMean.title)
    currMean = chkSt('means-dao', 'means')[realm.id][currMean.parentid]
  }
  var result = realm.title
  for(var i in chain){
    result = result + ' / ' + chain[i]
  }
  return result
}

const findMean = function(meanid){
  for(var realmid in chkSt('means-dao', 'means')){
    const means = chkSt('means-dao', 'means')[realmid]
    if(means[meanid]!=null){
      return means[meanid]
    }
  }
  return null
}

const isCurrentHquarter = function(hquarter){
  const todayTime = new Date().getTime()
  const beginTime = Date.parse(hquarter.startWeek.startDay)
  const endTime = Date.parse(hquarter.endWeek.endDay) + 86400000
  return todayTime>beginTime && todayTime<endTime
}

const isCurrentOrPrevHquarter = function(hquarter){
  if(isCurrentHquarter(hquarter)){
    return true;
  }
  const todayTime = new Date().getTime()
  const endTime = Date.parse(hquarter.endWeek.endDay) + 86400000
  return todayTime>endTime
}