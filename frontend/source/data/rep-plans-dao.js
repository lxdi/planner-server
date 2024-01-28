import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

import {createRep} from './common/repFactory'


export const createRepPlanRep = function(){
  createRep('repPlan-rep', '/repetition/plan')
}
