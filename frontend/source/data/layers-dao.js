//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep, basicListReceiving} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const INDEX_BY_MEAN = 'index-by-mean'
const GET_BY_MEAN_SPAN = 'getByMean'

export const createLayerRep = function(){
  createRep('layer-rep', 'layers', callback)
  //basicListReceiving('layer', 'by-mean-request', 'by-mean-response', '/get/by/mean', GET_BY_MEAN_SPAN, callback)
}

const callback = function(stSetter, spanName, arg){
  if(spanName == 'deleteSpan'){
    stSetter('objects', null)
    stSetter(INDEX_BY_MEAN, null)
  }
  if(spanName == GET_BY_MEAN_SPAN){
    stSetter(INDEX_BY_MEAN, createIndex(arg, 'meanid'))
  }
  if(spanName == 'cleanSpan'){
    stSetter(INDEX_BY_MEAN, null)
  }
}
