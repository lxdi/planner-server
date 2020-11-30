//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep, basicListReceiving} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const taskName = 'task'
const topicName = 'topic'
const tasktestingName = 'tasktesting'

const objMapName = 'objects'

const indexByMean = 'index-by-mean'
const getByMeanSpanName = 'getByMean'
const byMeanRequest = 'by-mean-request'
const byMeanResponse = 'by-mean-response'
const byMeanUrl = '/get/by/mean'

export const createTaskRep = function(){
  createRep(taskName, callback)
  basicListReceiving(taskName, byMeanRequest, byMeanResponse, byMeanUrl, getByMeanSpanName, callback)

  createRep(topicName, callback)
  basicListReceiving(topicName, byMeanRequest, byMeanResponse, byMeanUrl, getByMeanSpanName, callback)

  createRep(tasktestingName, callback)
  basicListReceiving(tasktestingName, byMeanRequest, byMeanResponse, byMeanUrl, getByMeanSpanName, callback)
}

const callback = function(stSetter, spanName, arg, pathVariable){
  if(spanName == 'getAllSpan'){
    throw 'Do not request all tasks'
  }
  if(spanName == 'deleteSpan'){
    stSetter(objMapName, null)
    stSetter(indexByMean, null)
  }
  if(spanName == getByMeanSpanName){
    stSetter(indexByMean, createIndexByMean(arg, pathVariable))
  }
  if(spanName == 'cleanSpan'){
    stSetter(indexByMean, null)
  }
}

const createIndexByMean = function(tasksMap, meanid){
  const index = {}
  index[meanid] = {}
  for(const id in tasksMap){
    index[meanid][id] = tasksMap[id]
  }
  return index
}
