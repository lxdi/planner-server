

export const formatDate = function(dateString){
  const date = new Date(dateString)
  return formatDateNumber(date.getDate()) + '.' + formatDateNumber(date.getMonth()+1) + '.' + date.getFullYear()
}

export const currentDateString = function(divisor){
  if(divisor==null){
    divisor = '.'
  }
  var today = new Date();
  return today.getFullYear()+divisor+formatDateNumber(today.getMonth())+divisor+formatDateNumber(today.getDate())
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
