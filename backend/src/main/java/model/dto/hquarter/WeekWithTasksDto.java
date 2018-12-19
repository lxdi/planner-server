package model.dto.hquarter;

import model.dto.task.TaskDtoLazy;
import model.entities.DaysOfWeek;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekWithTasksDto {

    Date startDay;
    Map<DaysOfWeek, List<TaskDtoLazy>> days = new HashMap<>();

    public Map<DaysOfWeek, List<TaskDtoLazy>> getDays() {
        return days;
    }

    public void setDays(Map<DaysOfWeek, List<TaskDtoLazy>> days) {
        this.days = days;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }
}
