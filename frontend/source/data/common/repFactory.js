import {sendGet, sendPost, sendPut, sendDelete} from '../postoffice'
import {registerObject, registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'


const repOffset = '-rep'
const objMapName = 'objects'

const getAllName = 'getAllSpan'
const allRequestEventName = 'all-request'
const allRequestReceivedEventName = 'all-response'
const allRequestUrlOffset = '/get/all'

const creationName = 'creationSpan'
const createEventName = 'create'
const createCompleteEventName = 'created'
const createUrlOffset = '/create'

const deleteName = 'deleteSpan'
const deleteEventName = 'delete'
const deleteCompleteEventName = 'deleted'
const deleteUrlOffset = '/delete'

export const createRep = function(name, callback){
  createGetAll(name, callback)
  createCreation(name, callback)
  createDeletion(name, callback)
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
  registerEvent(repName, createEventName, function(stateSetter, obj){
    sendPut('/' + name + createUrlOffset, JSON.stringify(obj), function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, objMapName)[""+receivedData.id] = receivedData

      if(callback!=null){
        callback(stateSetter, creationName, receivedData)
      }

      fireEvent(repName, createCompleteEventName, [receivedData])
    })
  })
  registerEvent(repName, createCompleteEventName, (stateSetter, obj) => obj)
}

const createDeletion = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, deleteEventName, function(stateSetter, obj){
    sendDelete('/' + name + deleteUrlOffset + '/' + obj.id, function() {
      delete chkSt(repName, objMapName)[obj.id]

      if(callback!=null){
        callback(stateSetter, deleteName, obj)
      }

      fireEvent(repName, deleteCompleteEventName, [obj])
    })
  })
  registerEvent(repName, deleteCompleteEventName, (stateSetter, obj) => obj)
}
