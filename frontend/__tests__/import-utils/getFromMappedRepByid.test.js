import {makeSplitMap, getFromMappedRepByid} from '../../source/utils/import-utils'

describe('Tests for getFromMappedRepByid method', ()=>{
  test('Simple test', ()=>{
    const obj1 = {id:1, parentid:1}
    const obj2 = {id:2, parentid:1}

    const obj3 = {id:3, parentid:2}

    const obj4 = {id:4, parentid:3}

    const testArr = [obj1, obj2, obj3, obj4]

    const testMap = makeSplitMap(testArr, 'id', 'parentid')

    expect(getFromMappedRepByid(testMap, 1)).toBe(obj1)
    expect(getFromMappedRepByid(testMap, 2)).toBe(obj2)
    expect(getFromMappedRepByid(testMap, 3)).toBe(obj3)
    expect(getFromMappedRepByid(testMap, 4)).toBe(obj4)
    expect(getFromMappedRepByid(testMap, 5)).toBeNull()

  })

})
