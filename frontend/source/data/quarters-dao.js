import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent, getStateVal} from '../controllers/eventor'


// export const QuartersState = {
//   quarters: null
// }

registerEvent('quarters-dao', 'quarters-request', function(stateSetter){
  $.ajax({url: "/quarter/all"}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importQuarters(stateSetter, receivedData)
            fireEvent('quarters-dao', 'quarters-received', [])
          });
})

registerEvent('quarters-dao', 'quarters-received', function(){})

const importQuarters = function(stateSetter, quartersDto){
  const quarters = {}
  quarters.__proto__ = quartersProto
  stateSetter('quarters', quarters)
  for(var i in quartersDto){
    quarters[""+quartersDto[i].id] = quartersDto[i]
  }
}

const quartersProto = {
  map: function(callback, filter){
    var result = []
    for (var i in this){
      if(this.hasOwnProperty(i)){
          if(filter!=null){
            if(filter(this[i])){
              result.push(callback(this[i]))
            }
          } else {
            result.push(callback(this[i]))
          }
      }
    }
    return result
  }
}