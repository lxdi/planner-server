package model.dao;

import model.entities.*;

import java.sql.Date;
import java.util.List;

public interface ITaskMappersDAO {

    void saveOrUpdate(TaskMapper taskMapper);
    void delete(TaskMapper taskMapper);
    TaskMapper taskMapperForTask(Task task);
    TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition);
    List<TaskMapper> taskMappersByWeeksAndSlotPositions(List<Week> weeks, List<SlotPosition> slotPositions);
    Date finishDateByTaskid(long taskid);
    List<TaskMapper> byWeekAndDay(Week week, DaysOfWeek daysOfWeek);

}
