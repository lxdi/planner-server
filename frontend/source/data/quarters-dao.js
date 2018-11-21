import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent} from '../controllers/eventor'


export const QuartersState = {
  quarters: null
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

registerEvent('quarters-dao', 'quarters-request', function(){
  $.ajax({url: "/quarter/all"}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importQuarters(receivedData)
            fireEvent('quarters-dao', 'quarters-received', [])
          });
})

registerEvent('quarters-dao', 'quarters-received', function(){

})

const importQuarters = function(quarters){
  QuartersState.quarters = {}
  QuartersState.quarters.__proto__ = quartersProto
  for(var i in quarters){
    QuartersState.quarters[""+quarters[i].id] = quarters[i]
  }
}
