import {swapLL} from '../../source/utils/linked-list'

describe('Tests for swapLL method', ()=>{
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

  test('1->2->3->4; swap 1 and 4', ()=>{
    expect(testNodes).not.toBeNull();
    expect(testNodes.length).toBe(5);

    swapLL(testNodes, testNodes[1], testNodes[4])

    expect(testNodes[1].nextid).toBeNull()
    expect(testNodes[4].nextid).toBe(2)
  })
})

describe('Test for swapLL on LL with size 2', ()=>{
  var testNodes = null
  beforeEach(() => {
    testNodes = []
    testNodes[1] = {id:1, nextid:2}
    testNodes[2] = {id:2, nextid:null}
  })

  test('(1<->2)', ()=>{
    swapLL(testNodes, testNodes[1], testNodes[2])

    expect(testNodes[2].nextid).toBe(1)
    expect(testNodes[1].nextid).toBeNull()
  })

  test('(2<->1)', ()=>{
    expect(testNodes[2].nextid).toBeNull()
    expect(testNodes[1].nextid).toBe(2)

    swapLL(testNodes, testNodes[2], testNodes[1])

    expect(testNodes[2].nextid).toBe(1)
    expect(testNodes[1].nextid).toBeNull()
  })

})

describe('Test for swapLL on LL with size 3', ()=>{
  var testNodes = null
  beforeEach(() => {
    testNodes = []
    testNodes[1] = {id:1, nextid:2}
    testNodes[2] = {id:2, nextid:3}
    testNodes[3] = {id:3, nextid:null}
  })

  afterEach(()=>{
    expect(testNodes.length).toBe(4)
  })

  test('(3<->1)', ()=>{
    swapLL(testNodes, testNodes[3], testNodes[1])

    expect(testNodes[3].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(1)
    expect(testNodes[1].nextid).toBeNull()
  })

  test('(2<->3)', ()=>{
    swapLL(testNodes, testNodes[2], testNodes[3])

    expect(testNodes[1].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(2)
    expect(testNodes[2].nextid).toBeNull()
  })

  test('(3<->2)', ()=>{
    swapLL(testNodes, testNodes[3], testNodes[2])

    expect(testNodes[1].nextid).toBe(3)
    expect(testNodes[3].nextid).toBe(2)
    expect(testNodes[2].nextid).toBeNull()
  })

  test('(1<->2)', ()=>{
    swapLL(testNodes, testNodes[1], testNodes[2])

    expect(testNodes[2].nextid).toBe(1)
    expect(testNodes[1].nextid).toBe(3)
    expect(testNodes[3].nextid).toBeNull()
  })

  test('(2<->1)', ()=>{
    swapLL(testNodes, testNodes[2], testNodes[1])

    expect(testNodes[2].nextid).toBe(1)
    expect(testNodes[1].nextid).toBe(3)
    expect(testNodes[3].nextid).toBeNull()
  })

  test('(2<->2)', ()=>{
    swapLL(testNodes, testNodes[2], testNodes[2])

    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(3)
    expect(testNodes[3].nextid).toBeNull()
  })

  test('(1<->1)', ()=>{
    swapLL(testNodes, testNodes[1], testNodes[1])

    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(3)
    expect(testNodes[3].nextid).toBeNull()
  })

  test('(3<->3)', ()=>{
    swapLL(testNodes, testNodes[3], testNodes[3])

    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBe(3)
    expect(testNodes[3].nextid).toBeNull()
  })

})

describe('Test for swapLL on LL 3->2->1', ()=>{
  var testNodes = null
  beforeEach(() => {
    testNodes = []
    testNodes[3] = {id:3, nextid:2}
    testNodes[2] = {id:2, nextid:1}
    testNodes[1] = {id:1, nextid:null}
  })

  afterEach(()=>{
    expect(testNodes.length).toBe(4)
  })

  test('(1<->2)', ()=>{
    swapLL(testNodes, testNodes[1], testNodes[2])

    expect(testNodes[3].nextid).toBe(1)
    expect(testNodes[1].nextid).toBe(2)
    expect(testNodes[2].nextid).toBeNull()
  })

})
