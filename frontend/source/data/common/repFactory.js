import {sendGet, sendPost, sendPut, sendDelete} from '../postoffice'
import {registerObject, registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'


//TODO should be
// entities-rep -> state {objects: {id: object}}
// GET /entities/all
// GET /entities/{id}
// GET /entities/{id}/full
// PUT /entities
// POST /entities
// POST /entities/list
// DELETE /entities

const REP_OFFSET = '-rep'
const OBJ_MAP_NAME = 'objects'

const GET_ALL_SPAN = 'getAllSpan'
const PUT_SPAN = 'creationSpan'
const DELETE_SPAN = 'deleteSpan'
const POST_SPAN = 'updateSpan'
const POST_LIST_SPAN = 'updateListSpan'
const GET_FULL_SPAN = 'getFullSpan'
const CLEAN_SPAN = 'cleanSpan'

export const createRep = function(repName, baseUrl, callback){
  getAllEvents(repName, baseUrl, callback)
  putEvents(repName, baseUrl, callback)
  deleteEvents(repName, baseUrl, callback)
  updateEvents(repName, baseUrl, callback)
  updateListEvents(repName, baseUrl, callback)
  getFullEvents(repName, baseUrl, callback)
  cleaning(repName, callback)
}

const basicListReceiving = function(repName, baseUrl, urlOffset, eventNameRequest, eventNameResponse, spanName, callback){

  registerEvent(repName, eventNameRequest, function(stateSetter){

      sendGet(baseUrl+urlOffset, function(data) {
                var objectsArr = typeof data == 'string'? JSON.parse(data): data
                const objMap = {}
                objectsArr.forEach(obj => objMap[obj.id]=obj)
                stateSetter(OBJ_MAP_NAME, objMap)

                if(callback!=null){
                  callback(stateSetter, spanName, objMap)
                }

                fireEvent(repName, eventNameResponse, [objMap])
              })
  })
  registerEvent(repName, eventNameResponse, (stSetter, objMap)=>objMap)
}

const getAllEvents = function(repName, baseUrl, callback){
  basicListReceiving(repName, baseUrl, '/all', 'all-request', 'all-response', GET_ALL_SPAN, callback)
}

const getFullEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'get-full', function(stateSetter, obj){

    sendGet(baseUrl + '/' + obj.id + '/full', function(data) {

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

const putEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'create', function(stateSetter, obj){

    sendPut(baseUrl, JSON.stringify(obj), function(data) {
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

const deleteEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'delete', function(stateSetter, obj){

    sendDelete(baseUrl + '/' + obj.id, function() {
      delete chkSt(repName, OBJ_MAP_NAME)[obj.id]

      if(callback!=null){
        callback(stateSetter, DELETE_SPAN, obj)
      }

      fireEvent(repName, 'deleted', [obj])
    })
  })

  registerEvent(repName, 'deleted', (stateSetter, obj) => obj)
}

const updateEvents = function(repName, baseUrl, callback){

  const afterResponseCallback = function(stateSetter, data) {
    var receivedData = typeof data == 'string'? JSON.parse(data): data
    chkSt(repName, OBJ_MAP_NAME)[""+receivedData.id] = receivedData

    if(callback!=null){
      callback(stateSetter, POST_SPAN, receivedData)
    }

    fireEvent(repName, 'updated', [receivedData])
  }

  const eventCallback = function(stateSetter, obj) {
    sendPost(baseUrl, JSON.stringify(obj), (data) => afterResponseCallback(stateSetter, data))
  }

  registerEvent(repName, 'update', (stateSetter, obj) => eventCallback(stateSetter, obj))
  registerEvent(repName, 'updated', (stateSetter, obj) => obj)
}

const updateListEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'update-list', (stateSetter, objList) => {

    sendPost(baseUrl+'/list', JSON.stringify(objList), (data) => {

      var receivedData = typeof data == 'string'? JSON.parse(data): data
      var objMap = chkSt(repName, OBJ_MAP_NAME)
      receivedData.forEach(obj => chkSt(repName, OBJ_MAP_NAME)[obj.id] = obj)

      if(callback!=null){
        callback(stateSetter, POST_LIST_SPAN, receivedData)
      }

      fireEvent(repName, 'updated', [receivedData])
    })
  })

  registerEvent(repName, 'updated-list', (stateSetter, obj) => obj)
}


const cleaning = function(repName, callback){
  registerEvent(repName, 'clean', function(stateSetter){
      stateSetter(OBJ_MAP_NAME, null)

      if(callback!=null){
        callback(stateSetter, CLEAN_SPAN)
      }
  })
}
