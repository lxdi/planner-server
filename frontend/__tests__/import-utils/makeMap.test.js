import {makeMap} from '../../source/utils/import-utils'

describe('Tests for makeMap method', ()=>{
  test('Simple test', ()=>{
    const obj1 = {id:1}
    const obj2 = {id:2}
    const obj3 = {id:3}
    const obj4 = {id:4}

    const testArr = [obj1, obj2, obj3, obj4]

    const result = makeMap(testArr, 'id')

    expect(result[1]).toBe(obj1)
    expect(result[2]).toBe(obj2)
    expect(result[3]).toBe(obj3)
    expect(result[4]).toBe(obj4)

  })

})
