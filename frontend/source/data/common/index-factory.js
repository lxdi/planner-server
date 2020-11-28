

export const createIndex = function(objs, indexFieldName){
  const index = {}
  for(const id in objs){
    const obj = objs[id]
    updateIndex(obj, index, indexFieldName)
  }
  return index
}

export const updateIndex = function(obj, index, indexFieldName){
  if(index[obj[indexFieldName]]==null){
    index[obj[indexFieldName]] = {}
  }
  index[obj[indexFieldName]][obj.id] = obj
}
