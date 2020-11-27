
export const Protomean = {
  toString: function(){
    return this.title + ' ' + this.targetsString();
  }
}

export var CreateMean = function(id, title, realmid, targets, children){
  var childrenToAdd = children!=null? children: []
  var newMean = {
    'id': id,
    'title': title,
    'realmid': realmid,
    'targets': targets,
    'children': childrenToAdd,
  }
  newMean.__proto__ = Protomean
  return newMean;
}

export var CreateTarget =  function(id, title, realmid, parentid){
  return {
    id: id,
    title: title,
    realmid: realmid,
    parentid: parentid
  }
}

export var CreateRealm =  function(id, title){
  return {
    'id': id,
    'title': title
  }
}
