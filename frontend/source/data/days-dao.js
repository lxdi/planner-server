import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

registerEvent('day-rep', 'get-one', (stSetter, day)=>{
  sendGet('/weeks/days/' + day.id, (data)=>{
      const dayMt = typeof data == 'string'? JSON.parse(data): data
      Object.assign(day, dayMt)
      day.isFull = true

      var objmap = chkSt('day-rep', 'objects')
      if(objmap == null){
        objmap = {}
        stSetter('objects', objmap)
      }
      objmap[day.id] = day
      fireEvent('day-rep', 'got-one', [day])
  })
})
registerEvent('day-rep', 'got-one', (stSetter, day)=>day)

registerEvent('day-rep', 'clean-all', (stSetter)=>{
  stSetter('objects', null)
})
