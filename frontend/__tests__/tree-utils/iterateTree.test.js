import {iterateTree} from '../../source/utils/tree-utils'

describe('Tests for iterateTree method', ()=>{
  test('2-level-depth tests', ()=>{
    const nodes = []

    nodes[1] = {id:1, nextid:2, parentid:null}
    nodes[2] = {id:2, nextid:null, parentid:null}

    nodes[3] = {id:3, nextid:4, parentid:1}
    nodes[4] = {id:4, nextid:null, parentid:1}

    const result = []
    iterateTree(nodes, (node)=>{result.push(node)})

    expect(result.length).toBe(4)
    expect(result[0]).toBe(nodes[1])
    expect(result[1]).toBe(nodes[3])
    expect(result[2]).toBe(nodes[4])
    expect(result[3]).toBe(nodes[2])
  })

  test('3-level-depth tests', ()=>{
    const nodes = []

    nodes[1] = {id:1, nextid:2, parentid:null}
    nodes[2] = {id:2, nextid:null, parentid:null}

    nodes[3] = {id:3, nextid:4, parentid:1}
    nodes[5] = {id:5, nextid:null, parentid:3}

    nodes[4] = {id:4, nextid:null, parentid:1}

    const result = []
    iterateTree(nodes, (node)=>{result.push(node)})

    expect(result.length).toBe(5)
    expect(result[0]).toBe(nodes[1])
    expect(result[1]).toBe(nodes[3])
    expect(result[2]).toBe(nodes[5])
    expect(result[3]).toBe(nodes[4])
    expect(result[4]).toBe(nodes[2])
  })

})
