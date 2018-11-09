
export const resolveTarget = function(objects, currentObject, protoObject){
  currentObject.children = []
  currentObject.__proto__ = protoObject
  for(var j in objects){
    if(objects[j].parentid == currentObject.id){
      currentObject.children.push(objects[j])
    }
  }
}
