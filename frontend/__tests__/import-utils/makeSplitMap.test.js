import {makeSplitMap} from '../../source/utils/import-utils'

describe('Tests for makeSplitMap method', ()=>{
  test('Simple test', ()=>{
    const obj1 = {id:1, parentid:1}
    const obj2 = {id:2, parentid:1}

    const obj3 = {id:3, parentid:2}

    const obj4 = {id:4, parentid:3}

    const testArr = [obj1, obj2, obj3, obj4]

    const result = makeSplitMap(testArr, 'id', 'parentid')

    expect(result[1][1]).toBe(obj1)
    expect(result[1][2]).toBe(obj2)
    expect(result[2][3]).toBe(obj3)
    expect(result[3][4]).toBe(obj4)

  })

})
