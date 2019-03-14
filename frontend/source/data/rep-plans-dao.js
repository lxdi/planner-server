import {registerEvent, registerReaction, fireEvent, viewStateVal} from '../controllers/eventor'
import {sendGet} from './postoffice'

const repName = 'rep-plans-dao'

registerEvent(repName, 'plans-request', (stateSetter)=>{
  sendGet('/repetition/plan/all', (data)=>{
      const repPlans = []
      data.forEach((rp)=>{repPlans[rp.id]=rp})
      stateSetter('rep-plans', repPlans)
      fireEvent(repName, 'plans-received')
  })
})

registerEvent(repName, 'plans-received', ()=>{})
