
export var state = {
   currState: "Targets",
   threeFrames: false
 }

var today = new Date();
export var CurrentDate = {
  day: today.getDate(),
  month: today.getMonth()+1, //January is 0!
  year: today.getFullYear()
}
