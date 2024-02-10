import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'
import {sendGet, sendPut, sendPost} from './postoffice'

const NAME = 'week'
const REP_NAME = NAME + '-rep'
const OBJECTS_LIST = 'objects-list'

registerEvent(REP_NAME, 'get-current-list', (stSetter)=>{
  sendGet('/weeks/current-year', (data)=>{
      const list = typeof data == 'string'? JSON.parse(data): data
      stSetter(OBJECTS_LIST, list)

      const objMap = {}
      list.forEach(week => {
        objMap[week.id] = week
      })
      stSetter('objects', objMap)
      fireEvent(REP_NAME, 'got-current-list', [list])
  })
})
registerEvent(REP_NAME, 'got-current-list', (stSetter, list)=>list)

registerEvent(REP_NAME, 'get-prev', (stSetter, current)=>{
  sendGet('/weeks/' + current.id + '/prev', (data)=>{
      const week = typeof data == 'string'? JSON.parse(data): data
      chkSt(REP_NAME, 'objects')[week.id] = week
      chkSt(REP_NAME, OBJECTS_LIST).unshift(week)
      fireEvent(REP_NAME, 'got-prev', [week])
  })
})
registerEvent(REP_NAME, 'got-prev', (stSetter, week)=>week)

registerEvent(REP_NAME, 'get-next', (stSetter, current)=>{
  sendGet('/weeks/' + current.id + '/next', (data)=>{
      const week = typeof data == 'string'? JSON.parse(data): data
      chkSt(REP_NAME, 'objects')[week.id] = week
      chkSt(REP_NAME, OBJECTS_LIST).push(week)
      fireEvent(REP_NAME, 'got-next', [week])
  })
})
registerEvent(REP_NAME, 'got-next', (stSetter, week)=>week)

registerEvent(REP_NAME, 'move-plans', (stSetter, movingDto)=>{
  sendPost('/weeks/move/plans', movingDto, ()=>{
      cleanRep(stSetter)
      fireEvent(REP_NAME, 'moved-plans')
  })
})
registerEvent(REP_NAME, 'moved-plans', (stSetter)=>{})

registerEvent(REP_NAME, 'clean-all', (stSetter)=>cleanRep(stSetter))


registerEvent(REP_NAME, 'assign-mean', (stSetter, dto)=>{
  sendPost('/weeks/schedule/mean', dto, ()=>{
    fireEvent(REP_NAME, 'clean-all')
    fireEvent(REP_NAME, 'assign-mean-done', [dto])
  })
})
registerEvent(REP_NAME, 'assign-mean-done', (stSetter, dto)=>dto)

registerEvent(REP_NAME, 'unschedule-mean', (stSetter, meanId)=>{
  sendPost('/weeks/unschedule/mean/'+meanId, null, ()=>{
    fireEvent(REP_NAME, 'clean-all')
    fireEvent(REP_NAME, 'unschedule-mean-done', [meanId])
  })
})
registerEvent(REP_NAME, 'unschedule-mean-done', (stSetter, meanId)=>meanId)


const cleanRep = function(stSetter){
  fireEvent('day-rep', 'clean-all')
  stSetter('objects', null)
  stSetter(OBJECTS_LIST, null)
}
