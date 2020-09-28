
const oneDayMs =  24 * 60 * 60 * 1000

export const formatDate = function(dateString){
  const date = new Date(dateString)
  return formatDateNumber(date.getDate()) + '.' + formatDateNumber(date.getMonth()+1) + '.' + date.getFullYear()
}

export const currentDateString = function(divisor){
  return dateToString(new Date(), divisor)
}

export const tomorrowDateString = function(divisor){
  return dateToString(new Date(new Date().getTime() + oneDayMs), divisor)
}

export const yesterdayDateString = function(divisor){
  return dateToString(new Date(new Date().getTime() - oneDayMs), divisor)
}


const dateToString = function(date, divisor){
  if(divisor==null){
    divisor = '.'
  }
  return date.getFullYear()
              +divisor+formatDateNumber(date.getMonth()+1)
              +divisor+formatDateNumber(date.getDate())
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
