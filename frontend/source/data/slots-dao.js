//import $ from 'jquery'
import {sendGet, sendPut, sendPost} from './postoffice'
import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'


export const createSlotsRep = function(){
  createRep('slot-rep', '/slot')
}
