package model.dao;

import model.entities.SlotPosition;
import model.entities.Task;
import model.entities.TaskMapper;
import model.entities.Week;

public interface ITaskMappersDAO {

    void saveOrUpdate(TaskMapper taskMapper);
    TaskMapper taskMapperForTask(Task task);
    TaskMapper taskMapperByWeekAndSlotPosition(Week week, SlotPosition slotPosition);

}
