//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep, basicListReceiving} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const name = 'layer'
const repName = name + '-rep'
const objMapName = 'objects'
const meanIdFieldName = 'meanid'

const indexByMean = 'index-by-mean'
const getByMeanSpanName = 'getByMean'
const byMeanRequest = 'by-mean-request'
const byMeanResponse = 'by-mean-response'
const byMeanUrl = '/get/by/mean'

export const createLayerRep = function(){
  createRep(name, callback)
  basicListReceiving(name, byMeanRequest, byMeanResponse, byMeanUrl, getByMeanSpanName, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'getAllSpan'){
    throw 'Do not request all layers'
  }
  if(spanName == 'deleteSpan'){
    stSetter(objMapName, null)
    stSetter(indexByMean, null)
  }
  if(spanName == getByMeanSpanName){
    stSetter(indexByMean, createIndex(arg, meanIdFieldName))
  }
  if(spanName == 'cleanSpan'){
    stSetter(indexByMean, null)
  }
}
