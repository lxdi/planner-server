package model.dto.hquarter;

import model.dto.task.TaskDtoLazy;
import model.entities.DaysOfWeek;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekWithTasksDto {

    Map<DaysOfWeek, List<TaskDtoLazy>> days = new HashMap<>();

    public Map<DaysOfWeek, List<TaskDtoLazy>> getDays() {
        return days;
    }

    public void setDays(Map<DaysOfWeek, List<TaskDtoLazy>> days) {
        this.days = days;
    }
}
