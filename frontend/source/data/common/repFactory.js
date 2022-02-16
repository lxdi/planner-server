import {sendGet, sendPost, sendPut, sendDelete} from '../postoffice'
import {registerObject, registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'


//TODO should be
// entities-rep -> state {objects: {id: object}}
// GET /entities/all
// GET /entities/{id}
// GET /entities/{id}/full
// PUT /entities
// POST /entities
// DELETE /entities

const REP_OFFSET = '-rep'
const OBJ_MAP_NAME = 'objects'

const GET_ALL_SPAN = 'getAllSpan'
const PUT_SPAN = 'creationSpan'
const DELETE_SPAN = 'deleteSpan'
const POST_SPAN = 'updateSpan'
const GET_FULL_SPAN = 'getFullSpan'
const CLEAN_SPAN = 'cleanSpan'
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
  const repName = name + REP_OFFSET
  const namePlural = name+'s'

  registerEvent(repName, eventNameRequest, function(stateSetter, pathVariable){
      const pathVariableOffset = pathVariable!=null? '/' + pathVariable: ''

      sendGet("/"+namePlural+urlRequestOffset+pathVariableOffset, function(data) {
                var objectsArr = typeof data == 'string'? JSON.parse(data): data
                const objMap = {}
                objectsArr.forEach(obj => objMap[obj.id]=obj)
                stateSetter(OBJ_MAP_NAME, objMap)

                if(callback!=null){
                  callback(stateSetter, spanName, objMap, pathVariable)
                }

                fireEvent(repName, eventNameResponse, [objMap])
              })
  })
  registerEvent(repName, eventNameResponse, (stSetter, objMap)=>objMap)
}

const createGetAll = function(name, callback){
  basicListReceiving(name, 'all-request', 'all-response', '/all', GET_ALL_SPAN, callback)
}

const createGetFull = function(name, callback){
  const repName = name + REP_OFFSET
  const namePlural = name+'s'

  registerEvent(repName, 'get-full', function(stateSetter, obj){

    sendGet('/' + namePlural + '/' + obj.id + '/full', function(data) {

      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, OBJ_MAP_NAME)[""+receivedData.id] = receivedData

      if(receivedData.id != obj.id){
        console.log(repName, obj, receivedData)
        throw "Get full: objects ids are not the same"
      }

      //const lazyObj = chkSt(repName, OBJ_MAP_NAME)[""+obj.id]
      Object.assign(obj, receivedData)
      obj['isFull'] = true

      if(callback!=null){
        callback(stateSetter, GET_FULL_SPAN, obj)
      }

      fireEvent(repName, 'got-full', [obj])
    })
  })

  registerEvent(repName, 'got-full', (stateSetter, obj) => obj)
}

const createCreation = function(name, callback){
  const repName = name + REP_OFFSET
  const url = '/'+name+'s'

  registerEvent(repName, 'create', function(stateSetter, obj){

    sendPut(url, JSON.stringify(obj), function(data) {
      var receivedData = typeof data == 'string'? JSON.parse(data): data
      chkSt(repName, OBJ_MAP_NAME)[""+receivedData.id] = receivedData

      if(callback!=null){
        callback(stateSetter, PUT_SPAN, receivedData)
      }

      fireEvent(repName, 'created', [receivedData])
    })
  })

  registerEvent(repName, 'created', (stateSetter, obj) => obj)
}

const createDeletion = function(name, callback){
  const repName = name + REP_OFFSET
  const namePlural = name+'s'

  registerEvent(repName, 'delete', function(stateSetter, obj){

    sendDelete('/' + namePlural + '/' + obj.id, function() {
      delete chkSt(repName, OBJ_MAP_NAME)[obj.id]

      if(callback!=null){
        callback(stateSetter, DELETE_SPAN, obj)
      }

      fireEvent(repName, 'deleted', [obj])
    })
  })

  registerEvent(repName, 'deleted', (stateSetter, obj) => obj)
}

const updateCreation = function(name, callback){
  const repName = name + REP_OFFSET
  const url = '/'+name+'s'

  const afterResponseCallback = function(data) {
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    chkSt(repName, OBJ_MAP_NAME)[""+receivedData.id] = receivedData

    if(callback!=null){
      callback(stateSetter, POST_SPAN, receivedData)
    }

    fireEvent(repName, 'updated', [receivedData])
  }

  const eventCallback = function(stateSetter, obj) {
    sendPost(url, JSON.stringify(obj), (data) => afterResponseCallback(data))
  }

  registerEvent(repName, 'update', (stateSetter, obj) => eventCallback(stateSetter, obj))
  registerEvent(repName, 'updated', (stateSetter, obj) => obj)
}


const cleaning = function(name, callback){
  const repName = name + REP_OFFSET
  registerEvent(repName, cleanEventName, function(stateSetter){
      stateSetter(OBJ_MAP_NAME, null)

      if(callback!=null){
        callback(stateSetter, CLEAN_SPAN)
      }
  })
}
