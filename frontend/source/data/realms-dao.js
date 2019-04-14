//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

registerEvent('realms-dao', 'realms-request', function(stateSetter){
  sendGet("/realm/all", (data)=>{
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    importRealms(stateSetter, receivedData)
    for(var i in chkSt('realms-dao', 'realms')){
      if(chkSt('realms-dao', 'currentRealm')==null){
        stateSetter('currentRealm', chkSt('realms-dao', 'realms')[i])
      }
      if(chkSt('realms-dao', 'realms')[i].current==true){
        stateSetter('currentRealm', chkSt('realms-dao', 'realms')[i])
      }
    }
    fireEvent('realms-dao', 'realms-received', [])
  })
})

registerEvent('realms-dao', 'realms-received', ()=>{})

registerEvent('realms-dao', 'create', function(stateSetter, realm){
  sendPut('/realm/create', JSON.stringify(realm), function(data) {
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    chkSt('realms-dao', 'realms')[""+receivedData.id] = receivedData
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
  if(chkSt('realms-dao', 'realms')==null){
    stateSetter('realms', [])
  }
  for(var i in data){
    chkSt('realms-dao', 'realms')[""+data[i].id] = data[i]
  }
}
