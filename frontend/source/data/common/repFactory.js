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

const updateName = 'updateSpan'
const updateEventName = 'update'
const updateCompleteEventName = 'updated'
const updateUrlOffset = '/update'

const getFullName = 'getFullSpan'
const getFullEventName = 'get-full'
const getFullCompleteEventName = 'got-full'
const getFullUrlOffset = '/get/full'
const fullFieldIndicator = 'isFull'

const cleanName = 'cleanSpan'
const cleanEventName = 'clean'

export const createRep = function(name, callback){
  createGetAll(name, callback)
  createCreation(name, callback)
  createDeletion(name, callback)
  updateCreation(name, callback)
  createGetFull(name, callback)
  cleaning(name, callback)
}

export const basicListReceiving = function(name, eventNameRequest, eventNameResponse, urlRequestOffset, spanName, callback){
  const repName = name + repOffset
  registerEvent(repName, eventNameRequest, function(stateSetter, pathVariable){
      const pathVariableOffset = pathVariable!=null? '/' + pathVariable: ''

      sendGet("/"+name+urlRequestOffset+pathVariableOffset, function(data) {
                var objectsArr = typeof data == 'string'? JSON.parse(data): data
                const objMap = {}
                objectsArr.forEach(obj => objMap[obj.id]=obj)
                stateSetter(objMapName, objMap)

                if(callback!=null){
                  callback(stateSetter, spanName, objMap, pathVariable)
                }

                fireEvent(repName, eventNameResponse, [objMap])
              })
  })
  registerEvent(repName, eventNameResponse, (stSetter, objMap)=>objMap)
}

const createGetAll = function(name, callback){
  basicListReceiving(name, allRequestEventName, allRequestReceivedEventName,
          allRequestUrlOffset, getAllName, callback)
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

const updateCreation = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, updateEventName, function(stateSetter, obj){
    sendPost('/' + name + updateUrlOffset, JSON.stringify(obj), function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, objMapName)[""+receivedData.id] = receivedData

      if(callback!=null){
        callback(stateSetter, updateName, receivedData)
      }

      fireEvent(repName, updateCompleteEventName, [receivedData])
    })
  })
  registerEvent(repName, updateCompleteEventName, (stateSetter, obj) => obj)
}

const createGetFull = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, getFullEventName, function(stateSetter, obj){
    sendGet('/' + name + getFullUrlOffset + '/' + obj.id, function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, objMapName)[""+receivedData.id] = receivedData

      if(receivedData.id != obj.id){
        console.log(repName, obj, receivedData)
        throw "Get full: objects ids are not the same"
      }

      //const lazyObj = chkSt(repName, objMapName)[""+obj.id]
      Object.assign(obj, receivedData)
      obj[fullFieldIndicator] = true

      if(callback!=null){
        callback(stateSetter, getFullName, obj)
      }

      fireEvent(repName, getFullCompleteEventName, [obj])
    })
  })
  registerEvent(repName, getFullCompleteEventName, (stateSetter, obj) => obj)
}


const cleaning = function(name, callback){
  const repName = name + repOffset
  registerEvent(repName, cleanEventName, function(stateSetter){
      stateSetter(objMapName, null)

      if(callback!=null){
        callback(stateSetter, cleanName)
      }
  })
}
