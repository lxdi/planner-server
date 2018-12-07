import {insertLL} from '../../source/utils/linked-list'

describe('Tests for insertLL method on 1->2->3->4', ()=>{

  var testNodes = null

  beforeEach(() => {
    testNodes = []
    const rawData = []
    rawData.push({id:4, nextid: null})
    rawData.push({id:3, nextid:4})
    rawData.push({id:2, nextid:3})
    rawData.push({id:1, nextid:2})

    for(var i in rawData){
      testNodes[rawData[i].id] = rawData[i]
    }
  });

  test('check test data', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);
    expect(testNodes[3].id).toBe(3)
    expect(testNodes[3].nextid).toBe(4)
  })

  test('1->2->3->4; insert 5 on the place of 3', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);

    const toInsert = {id:5}
    insertLL(testNodes, testNodes[3], toInsert)

    expect(testNodes.length).toBe(6)
    expect(testNodes[2].nextid).toBe(5)
    expect(testNodes[5].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(4)
    expect(testNodes[4].nextid).toBeNull()
  })

  test('1->2->3->4; insert 5 on the place of 4', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);

    const toInsert = {id:5}
    insertLL(testNodes, testNodes[4], toInsert)

    expect(testNodes.length).toBe(6)
    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(5)
    expect(testNodes[5].nextid).toBe(4)
    expect(testNodes[4].nextid).toBeNull()
  })

  test('1->2->3->4; insert 5 on the place of 1', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);

    const toInsert = {id:5}
    insertLL(testNodes, testNodes[1], toInsert)

    expect(testNodes.length).toBe(6)
    expect(testNodes[5].nextid).toBe(1)
    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(4)
    expect(testNodes[4].nextid).toBeNull()
  })

})

describe('Tests for insertLL method on 4->3->2->1', ()=>{

  var testNodes = null

  beforeEach(() => {
    testNodes = []
    const rawData = []
    rawData.push({id:1, nextid: null})
    rawData.push({id:2, nextid:1})
    rawData.push({id:3, nextid:2})
    rawData.push({id:4, nextid:3})

    for(var i in rawData){
      testNodes[rawData[i].id] = rawData[i]
    }
  });

  test('4->3->2->1; insert 5 on the place of 1', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);

    const toInsert = {id:5}
    insertLL(testNodes, testNodes[1], toInsert)

    expect(testNodes.length).toBe(6)
    expect(testNodes[4].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(5)
    expect(testNodes[5].nextid).toBe(1)
    expect(testNodes[1].nextid).toBeNull()
  })

})
