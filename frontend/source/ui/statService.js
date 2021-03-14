
const weekConstants = {
  max: 21,
  mid: 17,
  min: 13
}

const monthConstants = {
  max: weekConstants.max*4,
  mid: weekConstants.mid*4,
  min: weekConstants.min*4
}

const halfYearConstants = {
  max: weekConstants.max*25,
  mid: weekConstants.mid*25,
  min: weekConstants.min*25
}

const yearConstants = {
  max: weekConstants.max*50,
  mid: weekConstants.mid*50,
  min: weekConstants.min*50
}

export const getColorForProgressStat = function(num, timeBound){
  if(timeBound == 'week'){
    return getColor(num, weekConstants)
  }
  if(timeBound == 'month'){
    return getColor(num, monthConstants)
  }
  if(timeBound == 'halfYear'){
    return getColor(num, halfYearConstants)
  }
  if(timeBound == 'year'){
    return getColor(num, yearConstants)
  }
}

export const getPercents = function(num, timeBound){
  const max = getMax(timeBound)
  return Math.round((num/max)*100)
}

const getMax = function(timeBound){
  if(timeBound == 'week'){
    return weekConstants.max
  }
  if(timeBound == 'month'){
    return monthConstants.max
  }
  if(timeBound == 'halfYear'){
    return halfYearConstants.max
  }
  if(timeBound == 'year'){
    return yearConstants.max
  }
}

const getColor = function(num, bounds){
  if(num < bounds.min){
    return 'red'
  }
  if(num >= bounds.min && num < bounds.mid){
    return 'orange'
  }
  if(num >= bounds.mid && num <= bounds.max){
    return 'green'
  }
  if(num > bounds.max){
    return 'blue'
  }
}
