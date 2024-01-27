import React from 'react';
import ReactDOM from 'react-dom';
import {Table} from 'react-bootstrap'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {CommonModal} from './../../common/common-modal'
import {CommonCrudeTemplate} from './../../common/common-crud-template'
import {StatefulTextField} from '../../common/stateful-text-field'

const NEW_PREFIX = 'new_'
const getDefaultState = function() {return {isOpen:false, mode:{isStatic:false, isEdit:false}}}

export class SlotsModal extends React.Component {
  constructor(props){
    super(props)

    this.state = getDefaultState()
    registerEvent('slots-modal', 'open', (stateSetter) => this.setState({isOpen: true}))
    registerEvent('slots-modal', 'close', (stateSetter) => this.setState(getDefaultState()))

    registerReaction('slots-modal', 'slot-rep', ['all-response', 'updated-list'], ()=>this.setState({}))
  }

  render(){
    var content = null

    return <CommonModal isOpen = {this.state.isOpen}
              cancelHandler={()=>fireEvent('slots-modal', 'close')} title={"Slots"} styleClass='slots-modal-style'>
          {this.state.isOpen? crudContent(this):''}
      </CommonModal>
  }
}

const crudContent = function(comp) {
  return <CommonCrudeTemplate editing = {comp.state.mode}
                  changeEditHandler = {()=>saveHandler(comp)}>
                    {getContent(comp)}
        </CommonCrudeTemplate>
}

const saveHandler = function(comp) {
  comp.setState({})
  if(comp.state.mode.isEdit == true) {
    return
  }

  const slots = Object.values(chkSt('slot-rep','objects'))
  slots.forEach(slot => slot.hours = parseInt(slot.hours))
  fireEvent('slot-rep', 'update-list', [slots])
}

const getContent = function(comp){
  if(chkSt('slot-rep','objects') == null) {
    fireEvent('slot-rep', 'all-request')
    return 'Loading...'
  }

  const realmsMap = chkSt('realm-rep','objects')
  const realms = Object.values(realmsMap)
  var result = []
  realms.forEach(r => result.push(realmTr(comp, r)))
  return <div>
          <Table striped bordered condensed hover >
            <tbody>
              <tr>
                <td>Realm</td>
                <td>Monday</td>
                <td>Tuesday</td>
                <td>Wednesday</td>
                <td>Thursday</td>
                <td>Friday</td>
                <td>Saturuday</td>
                <td>Sunday</td>
              </tr>
              {noRealmTr(comp)}
              {result}
            </tbody>
            </Table>
        </div>
}

const realmTr = function(comp, realm) {
  return <tr id={realm.id}>
            <td>{realm.title}</td>
            {slotTd(comp, getOrCreateSlot(realm.id, 'mon'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'tue'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'wed'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'thu'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'fri'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'sat'))}
            {slotTd(comp, getOrCreateSlot(realm.id, 'sun'))}
        </tr>
}

const noRealmTr = function(comp) {
  return <tr>
          <td>Commons</td>
          {slotDescTd(comp, getOrCreateSlot(null, 'mon'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'tue'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'wed'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'thu'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'fri'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'sat'))}
          {slotDescTd(comp, getOrCreateSlot(null, 'sun'))}
        </tr>
}

const slotTd = function(comp, slot) {

  if (comp.state.mode.isEdit) {
    return <td>
            <a href="#" style = {{marginRight:'3px'}} onClick={()=>handleHours(comp, slot, 1)}>+</a>
            {slot.hours}
            <a href="#" style = {{marginLeft:'3px'}} onClick={()=>handleHours(comp, slot, -1)}>-</a>
          </td>
  } else 
    return <td>{slot.hours}</td> 
}

const slotDescTd = function(comp, slot) {
  return <td> 
            <StatefulTextField obj={slot} valName={'description'} isEdit={comp.state.mode.isEdit} onInput={()=>comp.setState({})}/>
          </td>
}

const handleHours = function(comp, slot, adjustment) {
  var adjust = slot.hours < 0 && adjustment < 0? 0: adjustment
  slot.hours = slot.hours + adjust
  comp.setState({})
}

const getSlotsByRealmId = function(realmId) {
  return Object.values(chkSt('slot-rep','objects')).filter(slot => slot.realmid == realmId)
}

const getOrCreateSlot = function(realmId, day) {
  const slots = getSlotsByRealmId(realmId)

  for (let i = 0; i < slots.length; i++) {
    if(slots[i].realmid == realmId && slots[i].dayOfWeek == day) {
      return slots[i]
    }
  }

  const newSlot = {id: NEW_PREFIX, realmid: realmId, dayOfWeek:day, hours: 0}
  chkSt('slot-rep','objects')[newSlot.id + realmId + day] = newSlot
  return newSlot
}
