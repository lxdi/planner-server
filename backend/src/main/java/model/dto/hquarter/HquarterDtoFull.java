package model.dto.hquarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HquarterDtoFull extends HquarterDtoLazy{

    List<Map<String, Object>> slots = new ArrayList<>();
    List<WeekWithTasksDto> weeks  = new ArrayList<>();

    public List<Map<String, Object>> getSlots() {
        return slots;
    }
    public void setSlots(List<Map<String, Object>> slots) {
        this.slots = slots;
    }

    public List<WeekWithTasksDto> getWeeks() {
        return weeks;
    }
    public void setWeeks(List<WeekWithTasksDto> weeks) {
        this.weeks = weeks;
    }
}
