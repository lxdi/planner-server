import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent} from '../controllers/eventor'

export const RealmsState = {
  realms: {},
  currentRealm: null,
  realmsLoaded: false
}

registerEvent('realms-dao', 'realms-request', function(){
  $.ajax({url: "/realm/all"}).then(function(data) {
            var receivedData = typeof data == 'string'? JSON.parse(data): data
            importRealms(receivedData)
            RealmsState.realmsLoaded = true
            RealmsState.currentRealm = RealmsState.realms[1]
            fireEvent('realms-dao', 'realms-received', [])
          });
})

registerEvent('realms-dao', 'realms-received', function(){

})

registerEvent('realms-dao', 'create', function(realm){
  $.ajax({
    url: '/realm/create',
    type: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(realm),
    success: function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      RealmsState.realms[""+receivedData.id] = receivedData
      fireEvent('realms-dao', 'realms-created', [realm])
    }
  });
})

registerEvent('realms-dao', 'realms-created', function(realm){
  return realm
})

const importRealms = function(data){
  for(var i in data){
    RealmsState.realms[""+data[i].id] = data[i]
  }
}

export var CreateRealm =  function(id, title){
  return {
    'id': id,
    'title': title
  }
}
