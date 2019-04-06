//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, viewStateVal} from 'absevent'

registerEvent('realms-dao', 'realms-request', function(stateSetter){
  sendGet("/realm/all", (data)=>{
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    importRealms(stateSetter, receivedData)
    for(var i in viewStateVal('realms-dao', 'realms')){
      if(viewStateVal('realms-dao', 'currentRealm')==null){
        stateSetter('currentRealm', viewStateVal('realms-dao', 'realms')[i])
      }
      if(viewStateVal('realms-dao', 'realms')[i].current==true){
        stateSetter('currentRealm', viewStateVal('realms-dao', 'realms')[i])
      }
    }
    fireEvent('realms-dao', 'realms-received', [])
  })
})

registerEvent('realms-dao', 'realms-received', ()=>{})

registerEvent('realms-dao', 'create', function(stateSetter, realm){
  sendPut('/realm/create', JSON.stringify(realm), function(data) {
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    viewStateVal('realms-dao', 'realms')[""+receivedData.id] = receivedData
    fireEvent('realms-dao', 'realm-created', [realm])
  })
})

registerEvent('realms-dao', 'realm-created', function(stateSetter, realm){
  return realm
})

registerEvent('realms-dao', 'change-current-realm', function(stateSetter, realm){
  sendPost('realm/setcurrent/'+realm.id, null, ()=>{
    stateSetter('currentRealm', realm)
    fireEvent('targets-frame', 'update')
    fireEvent('means-frame', 'update')
  })
})

const importRealms = function(stateSetter, data){
  if(viewStateVal('realms-dao', 'realms')==null){
    stateSetter('realms', [])
  }
  for(var i in data){
    viewStateVal('realms-dao', 'realms')[""+data[i].id] = data[i]
  }
}
