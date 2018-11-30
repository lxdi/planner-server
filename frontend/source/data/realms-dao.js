//import $ from 'jquery'
import {sendGet, sendPut} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'

registerEvent('realms-dao', 'realms-request', function(stateSetter){
  sendGet("/realm/all", (data)=>{
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    importRealms(stateSetter, receivedData)
    for(var i in viewStateVal('realms-dao', 'realms')){
      stateSetter('currentRealm', viewStateVal('realms-dao', 'realms')[i])
      break
    }
    fireEvent('realms-dao', 'realms-received', [])
  })
})

registerEvent('realms-dao', 'realms-received', ()=>{})

registerEvent('realms-dao', 'create', function(stateSetter, realm){
  sendPut('/realm/create', JSON.stringify(realm), function(data) {
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    viewStateVal('realms-dao', 'realms')[""+receivedData.id] = receivedData
    fireEvent('realms-dao', 'realms-created', [realm])
  })
})

registerEvent('realms-dao', 'realms-created', function(stateSetter, realm){
  return realm
})

registerEvent('realms-dao', 'change-current-realm', function(stateSetter, realm){
  stateSetter('currentRealm', realm)
})

const importRealms = function(stateSetter, data){
  if(viewStateVal('realms-dao', 'realms')==null){
    stateSetter('realms', [])
  }
  for(var i in data){
    viewStateVal('realms-dao', 'realms')[""+data[i].id] = data[i]
  }
}
