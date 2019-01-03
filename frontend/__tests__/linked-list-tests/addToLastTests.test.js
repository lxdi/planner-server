import {addToLastLL} from '../../source/utils/linked-list'

describe('Tests for addToLastLL method', ()=>{
  test('4-size nodes tests', ()=>{
    const testNodes = []
    const rawData = []
    rawData.push({id:4, nextid: null})
    rawData.push({id:3, nextid:4})
    rawData.push({id:2, nextid:3})
    rawData.push({id:1, nextid:2})

    const newNode = {id:5, nextid:null}

    for(var i in rawData){
      testNodes[rawData[i].id] = rawData[i]
    }

    addToLastLL(testNodes, newNode)

    expect(testNodes[5]).not.toBeNull()
    expect(testNodes[4].nextid).toBe(5)
  })

  test('empty list test', ()=>{
    const testNodes = []
    const newNode = {id:1}

    addToLastLL(testNodes, newNode)

    expect(testNodes[1]).not.toBeNull()
    expect(testNodes[1].nextid).toBeNull()
  })

  test('1-size list test', ()=>{
    const testNodes = []
    testNodes[1]={id:1}
    const newNode = {id:2}

    addToLastLL(testNodes, newNode)

    expect(testNodes[1]).not.toBeNull()
    expect(testNodes[1].nextid).toBe(2)
  })

})
