

export const formatDate = function(dateString){
  const date = new Date(dateString)
  return formatDateNumber(date.getDate()) + '.' + formatDateNumber(date.getMonth()+1) + '.' + date.getFullYear()
}

const formatDateNumber = function(num){
  if(num<10){
    return '0'+num
  } else {
    return num
  }
}
