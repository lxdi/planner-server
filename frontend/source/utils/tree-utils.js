import {resolveNodes} from './draggable-tree-utils'
import {iterateLLfull} from './linked-list'

export const iterateTree = function(nodes, callback){
  const resolvedNodes = resolveNodes(nodes)
  iterateLLfull(resolvedNodes.root, (node)=>{
    callback(node, 0)
    if(resolvedNodes.children[node.id]!=null){
      iterateChildren(resolvedNodes.children, node.id, callback, 1)
    }
  })
}

const iterateChildren = function(children, id, callback, level){
  iterateLLfull(children[id], (node)=>{
    callback(node, level)
    iterateChildren(children, node.id, callback, level+1)
  })
}
