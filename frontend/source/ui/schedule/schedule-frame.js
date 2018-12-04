import {addTaskButtonTitle} from './../../titles'
import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Table} from 'react-bootstrap'
import {CurrentDate} from './../../state'
import {HquarterModal} from './hquarter-modal'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../../controllers/eventor'


export class ScheduleFrame extends React.Component{
  constructor(props){
    super(props)
    this.state = {};
    registerReaction('schedule-frame', 'hquarters-dao', ['hquarters-received', 'hquarter-modified'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'means-dao', ['means-received', 'mean-modified'], ()=>this.setState({}))
    registerReaction('schedule-frame', 'realms-dao', 'change-current-realm', ()=>this.setState({}))
  }

  allowDrop(ev) {
    ev.preventDefault();
}

  render(){
    return(
      <div>
        <HquarterModal/>
        <div>
          {viewStateVal('means-dao', 'means')!=null?hquartersUI():null}
        </div>
      </div>
    )
  }
}

const hquartersUI = function(){
  if(viewStateVal('hquarters-dao', 'hquarters') != null){
    return viewStateVal('hquarters-dao', 'hquarters').map((hquarter)=>
          <Table striped bordered condensed hover width={'100px'} key={hquarter.year +'.'+formatDateNumber(hquarter.startday) + '.' + formatDateNumber(hquarter.startmonth)}>
            <tbody>
              <tr>
                <td>
                  <a href='#' onClick={()=>fireEvent('hquarter-modal', 'open', [hquarter])}>
                    {hquarter.year +'.'+formatDateNumber(hquarter.startday) + '.' + formatDateNumber(hquarter.startmonth)}
                  </a>
                </td>
              </tr>
              {getSlotsUI(hquarter)}
            </tbody>
          </Table>
      )
  } else {
    fireEvent('hquarters-dao', 'hquarters-request', [])
  }
}

const getSlotsUI = function(hquarter){
  const result = []
  for(var slotpos in hquarter.slots){
    const slot = hquarter.slots[slotpos]
    result.push(<tr onDragOver={(e)=>{e.preventDefault()}} onDrop={(e)=>fireEvent('means-dao', 'assign-quarter-to-draggable', [hquarter, 1])}>
                    <td>
                      Slot {slot.position}
                    </td>
                  </tr>)
  }
  return result
}

// const getMeanSlotUI = function(quarter, position){
//   const mean = getMean(quarter, position)
//   if(mean==null){
//     return <span style={{color: 'lightgrey'}}>slot {position}</span>
//   } else {
//     return <div>
//               <a href='#'>{mean.title}</a>
//               <a href='#' onClick={()=>fireEvent('means-dao', 'unassign-quarter', [mean])}> X</a>
//           </div>
//   }
// }
//
// const getMean = function(quarter, position){
//   const means = viewStateVal('means-dao', 'means')
//   for(var meanid in means){
//     const mean = means[meanid]
//     const currentRealm = viewStateVal('realms-dao', 'currentRealm')
//     if(currentRealm!=null && mean.realmid == currentRealm.id
//         && mean.quarterid == quarter.id
//         && mean.position == position){
//         return mean
//       }
//   }
//   return null
// }


const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
