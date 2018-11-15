import $ from 'jquery'
import {registerEvent, registerReaction, fireEvent} from '../controllers/eventor'
import {RealmsState} from './realms-dao'

export const TargetsState = {
  targets: {},
  targetsLoaded: false
}

// var targetsLoaded = false
// const targets = {}
TargetsState.targets.__proto__ = {
  map: function(callback, filter){
    var result = []
    for (var i in this){
      if(this.hasOwnProperty(i)){
          if(filter!=null){
            if(filter(this[i])){
              result.push(callback(this[i]))
            }
          } else {
            result.push(callback(this[i]))
          }
      }
    }
    return result
  }
}

const targetsuper = {
  toString: function(){
    return this.title;
  }
}

const importTargetsDto = function(targetsDto){
  for(var i in targetsDto){
    TargetsState.targets[""+targetsDto[i].id] = targetsDto[i]
  }
  resolveTargets();
}

const resolveTargets = function(){
  for(var i in TargetsState.targets){
    if(TargetsState.targets.hasOwnProperty(i)){
      resolveTarget(TargetsState.targets[i])
    }
  }
}

const resolveTarget = function(target){
  target.children = []
  target.__proto__ = targetsuper
  for(var j in TargetsState.targets){
    if(TargetsState.targets[j].parentid == target.id){
      target.children.push(TargetsState.targets[j])
    }
  }
}

registerEvent('targets-dao', 'create', function(target, parent){
  target.parentid = parent!=null? parent.id: null
  $.ajax({
    url: '/target/create',
    type: 'PUT',
    contentType: 'application/json',
    data: JSON.stringify(target),
    success: function(data) {
      TargetsState.targets[""+data.id] = data
      resolveTargets()
      fireEvent('targets-dao', 'target-created', [target])
    }
  });
})

registerEvent('targets-dao', 'target-created', function(target){
  return target
})

registerEvent('targets-dao', 'delete', function(id){
  $.ajax({
    url: '/target/delete/'+id,
    type: 'DELETE',
    success: function() {
      delete TargetsState.targets[id]
      resolveTargets()
      fireEvent('targets-dao', 'target-deleted', [id])
    }
  });
})

registerEvent('targets-dao', 'target-deleted', function(id){
  return id
})

registerEvent('targets-dao', 'modify', function(target){
  $.ajax({
    url: '/target/update',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(target),
    success: function(data) {
      TargetsState.targets[""+data.id] = data
      resolveTargets()
      fireEvent('targets-dao', 'target-modified', [target])
    }
  });
})

registerEvent('targets-dao', 'target-modified', function(target){
  return target
})

registerEvent('targets-dao', 'targets-request', function(){
  if(RealmsState.realmsLoaded){
    $.ajax({url: "/target/all/lazy"}).then(function(data) {
              var receivedData = typeof data == 'string'? JSON.parse(data): data
              importTargetsDto(receivedData)
              TargetsState.targetsLoaded = true
              fireEvent('targets-dao', 'targets-received', [])
            });
  } else {
    fireEvent('realms-dao', 'realms-request', [])
  }
})

registerEvent('targets-dao', 'targets-received', function(){

})

registerReaction('targets-dao', 'realms-dao', 'realms-received' , function(){
  fireEvent('targets-dao', 'targets-request', [])
})

// export var AllTargets = function(callback){
//   if(!targetsLoaded){
//     $.ajax({url: "/target/all/lazy"}).then(function(data) {
//               var receivedData = typeof data == 'string'? JSON.parse(data): data
//               importTargetsDto(receivedData)
//               targetsLoaded = true
//               fireEvent('targets-dao', 'targets-received', [])
//             });
//   }
//   return TargetsState.targets
// }

export var GetTargetById = function(id){
  return TargetsState.targets.id
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

// export var UpdateTarget = function(target, callback){
//   $.ajax({
//     url: '/target/update',
//     type: 'POST',
//     contentType: 'application/json',
//     data: JSON.stringify(target),
//     success: function(data) {
//       TargetsState.targets[""+data.id] = data
//       resolveTargets()
//       callback()
//       for(var i in ObserversUpdateTarget){
//         ObserversUpdateTarget[i](target)
//       }
//     }
//   });
// }
