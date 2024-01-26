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


registerEvent('day-rep', 'get-days-in-week', (stSetter, weekId)=>{

  var onResponse = (data)=>{
    const dayDtoList = typeof data == 'string'? JSON.parse(data): data
  
    var weeksMap = chkSt('day-rep', 'full-days-by-week')

    if(weeksMap == null){
      weeksMap = {}
      stSetter('full-days-by-week', weeksMap)
    }

    var daysMap = {}
    dayDtoList.forEach(obj => daysMap[obj.dayId]=obj)
    weeksMap[weekId] = daysMap
    fireEvent('day-rep',  'got-days-in-week')
}

  sendGet('/weeks/days/by/week/' + weekId, onResponse)

})

registerEvent('day-rep',  'got-days-in-week', (stSetter) => {})
