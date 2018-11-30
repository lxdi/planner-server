import axios from 'axios'

export const sendGet = function(url, callback){
  axios.get(url).then((data)=>callback(data.data))
}

export const sendPut = function(url, sendData, callback){
  sendWithData(url, sendData, callback, 'PUT')
}

export const sendPost = function(url, sendData, callback){
  sendWithData(url, sendData, callback, 'POST')
}

export const sendDelete = function(url, callback){
  axios({
    method: 'DELETE',
    url: url,
  }).then((data)=>callback(data.data));
}

const sendWithData = function(url, sendData, callback, type){
  axios({
    contentType: 'application/json',
    headers: {
      'Content-type': 'application/json'
    },
    method: type,
    url: url,
    data: sendData,
  }).then((data)=>callback(data.data));
}
