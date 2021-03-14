
export var CreateTarget =  function(id, title, realmid, parentid){
  return {
    id: id,
    title: title,
    realmid: realmid,
    parentid: parentid
  }
}

export var CreateMean = function(id, title, realmid, parentid){
  var newMean = {
    id: id,
    title: title,
    realmid: realmid,
    parentid: parentid
  }
  return newMean
}

export var CreateLayer =  function(id, priority, meanid){
  return {
    id: id,
    priority: priority,
    meanid: meanid
  }
}

export var CreateRealm =  function(id, title){
  return {
    'id': id,
    'title': title
  }
}

export const CreateTask = function(id, title, layerid){
  return {
    id: id,
    title: title,
    layerid: layerid
  }
}
