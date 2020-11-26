import {sendGet, sendPost, sendPut} from '../postoffice'
import {registerObject, registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'


const repOffset = '-rep'
const objMapName = 'objects'

const getAllName = 'getAllSpan'
const creationName = 'creationSpan'

const allRequestEventName = 'all-request'
const allRequestReceivedEventName = 'all-response'
const allRequestUrlOffset = '/get/all'

const createEventName = 'create'
const createCompleteEventName = 'created'
const createUrlOffset = '/create'

export const createRep = function(name, callback){
  createGetAll(name, callback)
  createCreation(name, callback)
}

const createGetAll = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, allRequestEventName, function(stateSetter){
      sendGet("/"+name+allRequestUrlOffset, function(data) {
                var objectsArr = typeof data == 'string'? JSON.parse(data): data
                const objMap = {}
                objectsArr.forEach(obj => objMap[obj.id]=obj)
                stateSetter(objMapName, objMap)

                if(callback!=null){
                  callback(stateSetter, getAllName, objMap)
                }

                fireEvent(repName, allRequestReceivedEventName, [objMap])
              })
  })
  registerEvent(repName, allRequestReceivedEventName, (stSetter, objMap)=>objMap)
}

const createCreation = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, createEventName, function(stateSetter, realm){
    sendPut('/' + name + createUrlOffset, JSON.stringify(realm), function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, objMapName)[""+receivedData.id] = receivedData

      if(callback!=null){
        callback(stateSetter, creationName, receivedData)
      }

      fireEvent(repName, createCompleteEventName, [realm])
    })
  })
  registerEvent(repName, createCompleteEventName, (stateSetter, realm) => realm)
}
