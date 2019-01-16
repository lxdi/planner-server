import {resolveNodes} from '../../source/utils/draggable-tree-utils'

const countFields = (obj)=>{var result = 0; for(var i in obj){result++};return result}

describe('Tests for resolveNodes method', ()=>{

  test('Simple test', ()=>{
    const nodes = []
    nodes[1] = {id:1, parentid:null}
    nodes[2] = {id:2, parentid:null}
    nodes[3] = {id:3, parentid:1}
    nodes[4] = {id:4, parentid:1}
    nodes[5] = {id:5, parentid:3}
    nodes[6] = {id:6, parentid:null}

    const resolved = resolveNodes(nodes)

    expect(countFields(resolved.root)).toBe(3)
    expect(countFields(resolved.children[1])).toBe(2)
    expect(countFields(resolved.children[6])).toBe(0)
  })

  test('1-size array test', ()=>{
    const nodes = []
    const node = {id:1}
    nodes[1] = node

    const resolved = resolveNodes(nodes)

    expect(resolved.root).not.toBeNull()
    expect(resolved.root[1]).toBe(node)

  })
})
