import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet} from './postoffice'

const repName = 'rep-plans-dao'

registerEvent(repName, 'plans-request', (stateSetter)=>{
  sendGet('/repetition/plans/all', (data)=>{
      const repPlans = []
      data.forEach((rp)=>{repPlans[rp.id]=rp})
      stateSetter('rep-plans', repPlans)
      fireEvent(repName, 'plans-received')
  })
})

registerEvent(repName, 'plans-received', ()=>{})
