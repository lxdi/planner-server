//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep, basicListReceiving} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

//const indexByMean = 'index-by-mean'
// const getByMeanSpanName = 'getByMean'
// const byMeanUrl = '/get/by/mean'

export const createTaskRep = function(){
  createRep('task', callback)
  //basicListReceiving(taskName, 'by-mean-request', 'by-mean-response', byMeanUrl, getByMeanSpanName, callback)

  createRep('topic', callback)
  //basicListReceiving(topicName, 'by-mean-request', 'by-mean-response', byMeanUrl, getByMeanSpanName, callback)

  createRep('task-testing', callback)
  //basicListReceiving(tasktestingName, 'by-mean-request', 'by-mean-response', byMeanUrl, getByMeanSpanName, callback)
}

const callback = function(stSetter, spanName, arg, pathVariable){
  if(spanName == 'getAllSpan'){
    throw 'Do not request all tasks'
  }
  if(spanName == 'deleteSpan'){
    stSetter('objects', null)
    //stSetter(indexByMean, null)
  }
  // if(spanName == getByMeanSpanName){
  //   stSetter(indexByMean, createIndexByMean(arg, pathVariable))
  // }
  // if(spanName == 'cleanSpan'){
  //   stSetter(indexByMean, null)
  // }
}

// const createIndexByMean = function(tasksMap, meanid){
//   const index = {}
//   index[meanid] = {}
//   for(const id in tasksMap){
//     index[meanid][id] = tasksMap[id]
//   }
//   return index
// }
