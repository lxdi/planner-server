//import $ from 'jquery'
import {sendGet} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'
import {normalizeInnerArrays} from './import-utils'

registerEvent('hquarters-dao', 'hquarters-request', function(stateSetter){
  sendGet("/hquarter/all", function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importHquarters(stateSetter, receivedData)
            fireEvent('hquarters-dao', 'hquarters-received', [])
          })
})

registerEvent('hquarters-dao', 'hquarters-received', function(){})

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
