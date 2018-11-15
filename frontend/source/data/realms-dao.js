import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent} from '../controllers/eventor'

export const RealmsState = {
  realms: [],
  realmsLoaded: false
}

registerEvent('realms-dao', 'realms-request', function(){
  $.ajax({url: "/realm/all"}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            RealmsState.realms = receivedData
            RealmsState.realmsLoaded = true
            fireEvent('realms-dao', 'realms-received', [])
          });
})

registerEvent('realms-dao', 'realms-received', function(){

})
