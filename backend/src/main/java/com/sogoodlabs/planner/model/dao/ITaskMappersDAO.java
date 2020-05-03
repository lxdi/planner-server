package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.*;

import java.sql.Date;
import java.util.List;

public interface ITaskMappersDAO {

    TaskMapper findOne(long id);
    void saveOrUpdate(TaskMapper taskMapper);
    void delete(TaskMapper taskMapper);
    TaskMapper taskMapperForTask(Task task);
    TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition);
    List<TaskMapper> taskMappersByWeeksAndSlotPositions(List<Week> weeks, List<SlotPosition> slotPositions);
    Date finishDateByTaskid(long taskid);
    List<TaskMapper> byWeekAndDay(Week week, DaysOfWeek daysOfWeek);
    List<TaskMapper> taskMappersOfHqAndBefore(HQuarter hQuarter, Date date);

}
