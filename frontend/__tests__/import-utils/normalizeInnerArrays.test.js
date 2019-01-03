import {normalizeInnerArrays} from '../../source/utils/import-utils'

describe('Tests for normalizeInnerArrays method', ()=>{
  test('1-level-depth tests', ()=>{
    const layers = [{priority:12}, {priority:1}, {priority:4}, {priority:2}]
    const obj = {layers:layers}

    normalizeInnerArrays(obj, [{arrName:'layers', posName:'priority'}])

    expect(obj.layers[12]).not.toBeNull()
    expect(obj.layers[12].priority).toBe(12)

    expect(obj.layers[1]).not.toBeNull()
    expect(obj.layers[1].priority).toBe(1)

    expect(obj.layers[2]).not.toBeNull()
    expect(obj.layers[2].priority).toBe(2)

    expect(obj.layers[4]).not.toBeNull()
    expect(obj.layers[4].priority).toBe(4)

  })

  test('2-level-depth tests', ()=>{
    const layers = [{priority:12, subjects:[{position:3}, {position:1}]}, {priority:1}, {priority:4, subjects:[{position:2}, {position:8}]}, {priority:2}]
    const obj = {layers:layers}

    normalizeInnerArrays(obj, [{arrName:'layers', posName:'priority'}, {arrName:'subjects', posName: 'position'}])

    expect(obj.layers[12]).not.toBeNull()
    expect(obj.layers[12].priority).toBe(12)
    expect(obj.layers[12].subjects[1].position).toBe(1)
    expect(obj.layers[12].subjects[3].position).toBe(3)

    expect(obj.layers[4].subjects[2].position).toBe(2)
    expect(obj.layers[4].subjects[8].position).toBe(8)

  })

})
