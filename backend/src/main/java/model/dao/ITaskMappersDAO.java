package model.dao;

import model.entities.SlotPosition;
import model.entities.Task;
import model.entities.TaskMapper;
import model.entities.Week;

import java.util.List;

public interface ITaskMappersDAO {

    void saveOrUpdate(TaskMapper taskMapper);
    void delete(TaskMapper taskMapper);
    TaskMapper taskMapperForTask(Task task);
    TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition);
    List<TaskMapper> taskMappersByWeeksAndSlotPositions(List<Week> weeks, List<SlotPosition> slotPositions);

}
