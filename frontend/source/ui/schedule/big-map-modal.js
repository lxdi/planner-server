import React from 'react';
import ReactDOM from 'react-dom';
import {CommonModal} from './../common-modal'
import {WeekSchedule} from './week-schedule'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'

export class BigMapModal extends React.Component {
  constructor(props){
    super(props)
    this.state = {isOpen:false}

    registerEvent('big-map-modal', 'open', ()=>this.setState({isOpen:true}))
    registerEvent('big-map-modal', 'close', ()=>{
      const hquarters = viewStateVal('hquarters-dao', 'hquarters')
      for(var id in hquarters){
        hquarters[id].isFull = false
      }
      this.setState({isOpen:false})
    })
    registerEvent('big-map-modal', 'update', ()=>this.setState({}))

  }

  render(){
    return <CommonModal
              isOpen = {this.state.isOpen}
              cancelHandler = {()=>fireEvent('big-map-modal', 'close')}
              title={'Big map'}
              styleClass='big-map-style'>
              {this.state.isOpen? hquartersUI():null}
      </CommonModal>
  }
}

const hquartersUI = function(){
  const result = []
  const hquarters = viewStateVal('hquarters-dao', 'hquarters')
  for(var dateid in hquarters){
    const hquarter = hquarters[dateid]
    if(hquarter.isFull==null || hquarter.isFull==false){
      fireEvent('hquarters-dao', 'hquarters-request-full')
      return 'Loading'
    }
    result.push(<div style={{marginTop:'5px', padding:'5px', border:'1px solid lightgrey', borderRadius:'10px'}}>
                  <div>{hquarter.startWeek.startDay}</div>
                  <WeekSchedule hquarter = {hquarter}/>
                </div>)
  }
  return <div> {result} </div>
}
