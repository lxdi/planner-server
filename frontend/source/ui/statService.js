
const weekConstants = {
  max: 18,
  mid: 14,
  min: 10
}

const monthConstants = {
  max: 72,
  mid: 56,
  min: 40
}

const halfYearConstants = {
  max: 450,
  mid: 350,
  min: 250
}

const yearConstants = {
  max: 900,
  mid: 700,
  min: 500
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
