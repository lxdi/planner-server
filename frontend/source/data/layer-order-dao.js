//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'
import {createIndex, updateIndex} from './common/index-factory'

const NAME = 'layer-order'
const REP_NAME = NAME + '-rep'

export const createMeanRep = function(){
  createRep('layer-order-rep', '/priorities', callback)
}

const callback = function(stSetter, spanName, arg){

}

registerEvent(REP_NAME, 'add-by-layer', (stSetter, layerId) => {
  sendPut('/layer-orders?layer-id='+layerId, null, (layerOrder) => {
    chkSt(REP_NAME, 'objects')[layerOrder.id] = layerOrder
    fireEvent(REP_NAME, 'added-by-layer')
  })
})
registerEvent(REP_NAME, 'added-by-layer', ()=>{})
