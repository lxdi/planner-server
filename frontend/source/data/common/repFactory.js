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
const GET_SPAN = 'getSpan'
const PUT_SPAN = 'creationSpan'
const DELETE_SPAN = 'deleteSpan'
const POST_SPAN = 'updateSpan'
const POST_LIST_SPAN = 'updateListSpan'
const GET_FULL_SPAN = 'getFullSpan'
const CLEAN_SPAN = 'cleanSpan'

export const createRep = function(repName, baseUrl, callback){
  getAllEvents(repName, baseUrl, callback)
  getEvents(repName, baseUrl, callback)
  putEvents(repName, baseUrl, callback)
  deleteEvents(repName, baseUrl, callback)
  updateEvents(repName, baseUrl, callback)
  updateListEvents(repName, baseUrl, callback)
  getFullEvents(repName, baseUrl, callback)
  cleaning(repName, callback)
}

const getAllEvents = function(repName, baseUrl, callback){
  basicListReceiving(repName, baseUrl, '/all', 'all-request', 'all-response', GET_ALL_SPAN, null, callback)
}

const getEvents = function(repName, baseUrl, callback){

  registerEvent(repName, 'get', function(stateSetter, id, paramsMap){
    sendGet(baseUrl + '/' + id+paramMapToUrl(paramsMap), function(data) {
      importObj(stateSetter, data, repName, 'got', GET_SPAN, callback)
    })
  })

  registerEvent(repName, 'got', (stateSetter, obj) => obj)
}

const getFullEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'get-full', function(stateSetter, obj, paramsMap){
    sendGet(baseUrl + '/' + obj.id + '/full'+paramMapToUrl(paramsMap), function(data) {
      importObj(stateSetter, data, repName, 'got-full', GET_SPAN, callback, obj => {obj['isFull'] = true})
    })
  })

  registerEvent(repName, 'got-full', (stateSetter, obj) => obj)
}

const putEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'create', function(stateSetter, obj, paramsMap){
    sendPut(baseUrl+paramMapToUrl(paramsMap), JSON.stringify(obj), function(data) {
      importObj(stateSetter, data, repName, 'created', PUT_SPAN, callback)
    })
  })

  registerEvent(repName, 'created', (stateSetter, obj) => obj)
}

const deleteEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'delete', function(stateSetter, obj, paramsMap){

    sendDelete(baseUrl + '/' + obj.id + paramMapToUrl(paramsMap), function() {
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

  registerEvent(repName, 'update', (stateSetter, obj) => {
    sendPost(baseUrl, JSON.stringify(obj), (data) => importObj(stateSetter, data, repName, 'updated', POST_SPAN, callback))
  })

  registerEvent(repName, 'updated', (stateSetter, obj) => obj)
}

const updateListEvents = function(repName, baseUrl, callback){
  registerEvent(repName, 'update-list', (stateSetter, objList, paramsMap) => {
    sendPost(baseUrl+'/list'+ paramMapToUrl(paramsMap), JSON.stringify(objList), (data) => {
      importObjList(stateSetter, data, repName, 'updated-list', POST_LIST_SPAN, callback)
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

export const basicListReceiving = function(repName, baseUrl, urlOffset, eventNameRequest, eventNameResponse, spanName, defaultParams, callback){

  registerEvent(repName, eventNameRequest, function(stSetter, paramsMap){
      sendGet(baseUrl+urlOffset+paramMapToUrl(paramsMap, defaultParams), function(data) {
              importObjList(stSetter, data, repName, eventNameResponse, spanName, callback)
          })
  })

  registerEvent(repName, eventNameResponse, (stSetter, objMap)=>objMap)
}

const importObjList = function(stSetter, data, repName, rsEvent, spanName, callback){
  var objMap = chkSt(repName, OBJ_MAP_NAME)

  if(objMap==null){
    objMap = {}
    stSetter(OBJ_MAP_NAME, objMap)
  }

  var objectsArr = typeof data == 'string'? JSON.parse(data): data
  objectsArr.forEach(obj => objMap[obj.id]=obj)

  if(callback!=null){
    callback(stSetter, spanName, objectsArr)
  }

  fireEvent(repName, rsEvent, [objectsArr])
}

const importObj = function(stSetter, data, repName, rsEvent, spanName, callback, decoratorCallback){
  var obj = typeof data == 'string'? JSON.parse(data): data
  var objMap = chkSt(repName, OBJ_MAP_NAME)

  if(objMap==null){
    objMap = {}
    stSetter(OBJ_MAP_NAME, objMap)
  }

  objMap[obj.id] = obj

  if(decoratorCallback!=null){
    decoratorCallback(obj)
  }

  if(callback!=null){
    callback(stSetter, spanName, obj)
  }

  fireEvent(repName, rsEvent, [obj])
}

const paramMapToUrl = function(paramsMap, defaultParams){
  if(paramsMap == null && defaultParams == null){
    return ''
  }

  if(defaultParams == null){
    return '?'+new URLSearchParams(paramsMap).toString()
  }

  if(paramsMap == null){
    return '?'+new URLSearchParams(defaultParams).toString()
  }

  return '?'+ new URLSearchParams(defaultParams).toString() + '&' + new URLSearchParams(paramsMap).toString()
}
