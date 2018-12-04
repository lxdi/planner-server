//import $ from 'jquery'
import {sendGet, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'
import {normalizeInnerArrays, getMaxVal} from './import-utils'

registerEvent('hquarters-dao', 'hquarters-request', function(stateSetter){
  sendGet("/hquarter/all", function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importHquarters(stateSetter, receivedData)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

registerEvent('hquarters-dao', 'hquarters-received', function(){})

registerEvent('hquarters-dao', 'update', (stateSetter, hquarter)=>{
  sendPost('/hquarter/update', hquarter, (data)=>{
    //var receivedData = typeof data == 'string'? JSON.parse(data): data
    viewStateVal('hquarters-dao', 'hquarters')[data.id] = data
    fireEvent('hquarters-dao', 'hquarter-modified', [data])
  })
})
registerEvent('hquarters-dao', 'hquarter-modified', (stateSetter, hquarter)=>hquarter)

registerEvent('hquarters-dao', 'add-slot', (stateSetter, hquarter)=>{
  //const nextPos = 
  const slot = {position: getMaxVal(hquarter.slots, 'position')+1}
  hquarter.slots[slot.position] = slot
})

const importHquarters = function(stateSetter, hquartersDto){
  const hquarters = []
  //quarters.__proto__ = quartersProto
  stateSetter('hquarters', hquarters)
  for(var i in hquartersDto){
    normalizeInnerArrays(hquartersDto[i], [{
      arrName:'slots',
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
