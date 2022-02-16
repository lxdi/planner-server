import {registerEvent, registerReaction, fireEvent, chkSt} from 'absevents'

registerEvent('drag-n-drop', 'put', (stSetter, type, data) => {
  stSetter('type', type)
  stSetter('data', data)
})

registerEvent('drag-n-drop', 'clean', (stSetter) => {
  stSetter('type', null)
  stSetter('data', null)
})
