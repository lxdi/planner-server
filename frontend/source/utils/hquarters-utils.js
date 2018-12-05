

export const findSlotInPosition = function(hquarter, day, position){
  for(var slotpos in hquarter.slots){
    const slot = hquarter.slots[slotpos]
    if(slot != null){
      for(var j in slot.slotPositions){
        const slotPosition = slot.slotPositions[j]
        if(slotPosition.dayOfWeek==day && slotPosition.position == position){
          return {slot:slot, slotPosition: slotPosition}
        }
      }
    }
  }
}
