import {findPrev} from '../../source/utils/linked-list'

describe('Tests for insertLL method', ()=>{
  test('4-size nodes tests', ()=>{
    const testNodes = []
    const rawData = []
    rawData.push({id:4, nextid: null})
    rawData.push({id:3, nextid:4})
    rawData.push({id:2, nextid:3})
    rawData.push({id:1, nextid:2})

    for(var i in rawData){
      testNodes[rawData[i].id] = rawData[i]
    }

    expect(findPrev(testNodes, testNodes[1])).toBeNull()
    expect(findPrev(testNodes, testNodes[2]).id).toBe(1)
    expect(findPrev(testNodes, testNodes[3]).id).toBe(2)
    expect(findPrev(testNodes, testNodes[4]).id).toBe(3)
  })

  test('2-size nodes tests', ()=>{
    const testNodes = []
    const rawData = []
    rawData.push({id:2, nextid:null})
    rawData.push({id:1, nextid:2})

    for(var i in rawData){
      testNodes[rawData[i].id] = rawData[i]
    }

    expect(findPrev(testNodes, testNodes[1])).toBeNull()
    expect(findPrev(testNodes, testNodes[2]).id).toBe(1)
  })

})
