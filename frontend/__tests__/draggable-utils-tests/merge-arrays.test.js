import {mergeArrays} from '../../source/utils/draggable-utils'

describe('Tests for mergeArrays', ()=>{
  test('Simple merge', ()=>{
    const elem1 = {}
    const elem2 = {}
    const elem3 = {}
    const arr1 = [elem1, elem2]
    const arr2 = [elem1, elem3]

    mergeArrays(arr1, arr2)

    expect(arr1.length).toBe(3)
    expect(arr2.length).toBe(2)
  })
})
