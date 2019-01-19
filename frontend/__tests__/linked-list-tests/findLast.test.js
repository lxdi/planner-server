import {findLast} from '../../source/utils/linked-list'

describe('Tests for findLast method', ()=>{
  test('1-size nodes tests', ()=>{
    const testNodes = []
    const firstNode = {id:0}
    testNodes[0] = firstNode

    expect(findLast(testNodes)).toBe(firstNode)
  })

})
