import $ from 'jquery'

export const sendGet = function(url, callback){
  $.ajax({url: url}).then((data)=>callback(data));
}

export const sendPut = function(url, sendData, callback){
  sendWithData(url, sendData, callback, 'PUT')
}

export const sendPost = function(url, sendData, callback){
  sendWithData(url, sendData, callback, 'POST')
}

export const sendDelete = function(url, callback){
  $.ajax({
    url: url,
    type: 'DELETE',
    success: (data)=>callback(data)
  });
}

const sendWithData = function(url, sendData, callback, type){
  $.ajax({
    url: url,
    type: type,
    contentType: 'application/json',
    data: sendData,
    success: (data)=>callback(data)
  });
}
