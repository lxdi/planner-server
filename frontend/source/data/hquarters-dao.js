//import $ from 'jquery'
import {sendGet, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

import {normalizeInnerArrays, getMaxVal} from '../utils/import-utils'
import {findSlotInPosition} from '../utils/hquarters-utils'

registerEvent('hquarters-dao', 'hquarters-request', function(stateSetter){
  sendGet("/hquarter/all", function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importHquarters(stateSetter, receivedData)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

registerEvent('hquarters-dao', 'hquarters-received', function(){})

registerEvent('hquarters-dao', 'hquarters-clean', (stateSetter)=>stateSetter('hquarters', []))

registerEvent('hquarters-dao', 'update', (stateSetter, hquarter)=>{
  sendPost('/hquarter/update', hquarter, (data)=>{
    //var receivedData = typeof data == 'string'? JSON.parse(data): data
    viewStateVal('hquarters-dao', 'hquarters')[data.id] = data
    fireEvent('hquarters-dao', 'hquarter-modified', [data])
  })
})
registerEvent('hquarters-dao', 'hquarter-modified', (stateSetter, hquarter)=>hquarter)

registerEvent('hquarters-dao', 'add-slot', (stateSetter, hquarter)=>{
  const slot = {position: getMaxVal(hquarter.slots, 'position')+1}
  hquarter.slots[slot.position] = slot
})

registerEvent('hquarters-dao', 'add-draggable', (stateSetter, hquarter, slot, slotPosition)=>stateSetter('draggableSlot', {hquarter: hquarter, slot:slot, slotPosition: slotPosition}))
registerEvent('hquarters-dao', 'remove-draggable', (stateSetter)=>stateSetter('draggableSlot', null))

registerEvent('hquarters-dao', 'assign-slot', (stateSetter, day, position)=>{
  const slotPosition = viewStateVal('hquarters-dao', 'draggableSlot').slotPosition
  if(slotPosition!=null){
    slotPosition.dayOfWeek = day
    slotPosition.position = position
  } else {
    const hquarter = viewStateVal('hquarters-dao', 'draggableSlot').hquarter
    const draggableSlot = viewStateVal('hquarters-dao', 'draggableSlot').slot
    if(draggableSlot!=null && findSlotInPosition(hquarter, day, position)==null
        && (draggableSlot.slotPositions==null || draggableSlot.slotPositions.length<3)){
      const slotPosition = {dayOfWeek: day, position: position}
      if(draggableSlot.slotPositions == null){
        draggableSlot.slotPositions = []
      }
      draggableSlot.slotPositions.push(slotPosition)
    }
  }
})

registerEvent('hquarters-dao', 'assign-mean-to-slot', (stateSetter, mean, slot)=>{
  sendPost('/hquarter/assignmean/'+mean.id+'/toslot/'+slot.id, null, ()=>{
    slot.meanid = mean.id
    fireEvent('hquarters-dao', 'hquarters-request')
  })
})

registerEvent('hquarters-dao', 'unassign-mean', (stateSetter, slot)=>{
  sendPost('/hquarter/slot/unassign/'+slot.id, null, ()=>{
    slot.meanid = null
    fireEvent('hquarters-dao', 'hquarters-request')
  })
})

registerEvent('hquarters-dao', 'request-for-default', (stateSetter)=>{
  sendGet('/hquarter/get/default', function(data){
    stateSetter('default', data)
    fireEvent('hquarters-dao', 'default-received', [data])
  })
})
registerEvent('hquarters-dao', 'default-received', (hquarter)=>hquarter)

registerEvent('hquarters-dao', 'update-default', (stateSetter)=>{
  sendPost('/hquarter/set/default', JSON.stringify(viewStateVal('hquarters-dao', 'default')), (data)=>{
    stateSetter('default', data)
    fireEvent('hquarters-dao', 'hquarters-request')
  })
})

registerEvent('hquarters-dao', 'get-full', (stateSetter, id)=>{
  sendGet('/hquarter/get/'+id, (hquarterfull)=>{
    hquarterfull.isfull=true
    Object.assign(viewStateVal('hquarters-dao', 'hquarters')[hquarterfull.id], hquarterfull)
    fireEvent('hquarters-dao', 'got-full', [viewStateVal('hquarters-dao', 'hquarters')[hquarterfull.id]])
  })
})
registerEvent('hquarters-dao', 'got-full', (stateSetter, hquarterfull)=>hquarterfull)


const importHquarters = function(stateSetter, hquartersDto){
  const hquarters = []
  stateSetter('hquarters', hquarters)
  for(var i in hquartersDto){
    normalizeInnerArrays(hquartersDto[i], [{
      arrName:'slotsLazy',
      posName: 'position'
    }])
    hquarters[""+hquartersDto[i].id] = hquartersDto[i]
  }
}

// const quartersProto = {
//   map: function(callback, filter){
//     var result = []
//     for (var i in this){
//       if(this.hasOwnProperty(i)){
//           if(filter!=null){
//             if(filter(this[i])){
//               result.push(callback(this[i]))
//             }
//           } else {
//             result.push(callback(this[i]))
//           }
//       }
//     }
//     return result
//   }
// }
