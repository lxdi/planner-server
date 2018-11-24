
export const Protomean = {
  targetsString: function(){
    var targetsString = '';
    var divisor = ' #';
    for(var i in this.targets){
      targetsString = targetsString +divisor+ this.targets[i].toString();
    }
    return targetsString
  },
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

export var CreateTarget =  function(id, title, realmid){
  //var childrenToAdd = children!=null? children:[];
  return {
    'id': id,
    'title': title,
    'realmid': realmid,
    //'children': childrenToAdd,
    toString: function(){
      return this.title;
    }
  }
}

export var CreateRealm =  function(id, title){
  return {
    'id': id,
    'title': title
  }
}
