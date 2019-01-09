//import $ from 'jquery'
import {sendGet, sendPost} from './postoffice'
import {registerObject, registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

import {normalizeInnerArrays, getMaxVal} from '../utils/import-utils'
import {findSlotInPosition} from '../utils/hquarters-utils'

registerObject('hquarters-dao', {default:{}})

registerEvent('hquarters-dao', 'hquarters-request', function(stateSetter){
  sendGet("/hquarter/currentlist", function(data) {
            importHquarters(stateSetter, data)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

registerEvent('hquarters-dao', 'hquarters-received', function(){})

registerEvent('hquarters-dao', 'hquarters-prev', (stateSetter, hquarter)=>{
  sendGet('/hquarter/prev/'+hquarter.id + '/1', function(data) {
            importHquarters(stateSetter, data)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

registerEvent('hquarters-dao', 'hquarters-next', (stateSetter, hquarter)=>{
  if(hquarter==null){
    hquarter = viewStateVal('hquarters-dao', 'hquarters')[findLastHquarter(viewStateVal('hquarters-dao', 'hquarters'))]
  }
  sendGet('/hquarter/next/'+hquarter.id + '/1', function(data) {
            importHquarters(stateSetter, data)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

// registerEvent('hquarters-dao', 'hquarters-clean', (stateSetter)=>{
//   stateSetter('firstHquarterId', null)
//   stateSetter('hquarters', null)
// })

registerEvent('hquarters-dao', 'update', (stateSetter, hquarter)=>{
  sendPost('/hquarter/update', hquarter, (data)=>{
    fireEvent('hquarters-dao', 'hquarter-modified', [Object.assign(viewStateVal('hquarters-dao', 'hquarters')[Date.parse(data.startWeek.startDay)], data)])
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
    Object.assign(viewStateVal('hquarters-dao', 'default'), data)
    viewStateVal('hquarters-dao', 'default').isFull=true
    fireEvent('hquarters-dao', 'default-received', [viewStateVal('hquarters-dao', 'default')])
  })
})

registerEvent('hquarters-dao', 'default-received', (hquarter)=>hquarter)

registerEvent('hquarters-dao', 'update-default', (stateSetter)=>{
  sendPost('/hquarter/set/default', JSON.stringify(viewStateVal('hquarters-dao', 'default')), (data)=>{
    Object.assign(viewStateVal('hquarters-dao', 'default'), data)
    viewStateVal('hquarters-dao', 'default').isFull=false
    fireEvent('hquarters-dao', 'hquarters-request')
  })
})

registerEvent('hquarters-dao', 'get-full', (stateSetter, id)=>{
  sendGet('/hquarter/get/'+id, (hquarterfull)=>{
    hquarterfull.isFull=true
    const idDate = Date.parse(hquarterfull.startWeek.startDay)
    Object.assign(viewStateVal('hquarters-dao', 'hquarters')[idDate], hquarterfull)
    fireEvent('hquarters-dao', 'got-full', [viewStateVal('hquarters-dao', 'hquarters')[idDate]])
  })
})

registerEvent('hquarters-dao', 'got-full', (stateSetter, hquarterfull)=>hquarterfull)


const importHquarters = function(stateSetter, hquartersDto){
  var hquarters = viewStateVal('hquarters-dao', 'hquarters')
  if(hquarters==null){
    hquarters = []
    stateSetter('hquarters', hquarters)
  }
  const last = hquarters[findLastHquarter(hquarters)]
  const importedResult = importToArray(hquartersDto, hquarters)
  spliceIfBefore(importedResult.last)
  spliceIfAfter(last, importedResult.first)
  stateSetter('firstHquarterId', findFirstHquarter(hquarters))
}

const importToArray = function(hquartersDto, hquarters){
  var lastHq = null
  var firstHq = null
  for(var i in hquartersDto){
    normalizeInnerArrays(hquartersDto[i], [{
      arrName:'slotsLazy',
      posName: 'position'
    }])
    if(hquarters[Date.parse(hquartersDto[i].startWeek.startDay)]!=null){
      Object.assign(hquarters[Date.parse(hquartersDto[i].startWeek.startDay)], hquartersDto[i])
    } else {
      hquarters[Date.parse(hquartersDto[i].startWeek.startDay)] = hquartersDto[i]
      const previdx = i-1
      const nextidx = parseInt(i)+1
      hquartersDto[i].previd = hquartersDto[previdx]!=null?Date.parse(hquartersDto[previdx].startWeek.startDay):null
      hquartersDto[i].nextid = hquartersDto[nextidx]!=null?Date.parse(hquartersDto[nextidx].startWeek.startDay):null
    }
    if(firstHq==null){
      firstHq = hquarters[Date.parse(hquartersDto[i].startWeek.startDay)]
    }
    lastHq = hquarters[Date.parse(hquartersDto[i].startWeek.startDay)]
  }
  return {first:firstHq, last:lastHq}
}

const spliceIfBefore = function(last){
  if(viewStateVal('hquarters-dao', 'firstHquarterId')!=null && Date.parse(last.startWeek.startDay)<viewStateVal('hquarters-dao', 'firstHquarterId')){
    last.nextid = viewStateVal('hquarters-dao', 'firstHquarterId')
    viewStateVal('hquarters-dao', 'hquarters')[viewStateVal('hquarters-dao', 'firstHquarterId')].previd
          = Date.parse(last.startWeek.startDay)
  }
}

const spliceIfAfter = function(lastBeforeImport, firstInNewImport){
  if(lastBeforeImport!=null && Date.parse(lastBeforeImport.startWeek.startDay)<Date.parse(firstInNewImport.startWeek.startDay)){
    lastBeforeImport.nextid = Date.parse(firstInNewImport.startWeek.startDay)
    firstInNewImport.previd = Date.parse(lastBeforeImport.startWeek.startDay)
  }
}

const findFirstHquarter = function(hquarters){
  return findExtrem(hquarters, (result, curhqId)=>result>curhqId)
}

const findLastHquarter = function(hquarters){
  return findExtrem(hquarters, (result, curhqId)=>result<curhqId)
}

// returns ID !!!
const findExtrem = function(hquarters, compareCallback){
  var result = null
  for(var i in hquarters){
    const curhqId = Date.parse(hquarters[i].startWeek.startDay)
    if(result==null || compareCallback(result, curhqId)){
      result = curhqId
    }
  }
  return result
}
