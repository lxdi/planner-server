package model.dao;

import model.dao.ITaskMappersDAO;
import model.dao.ITasksDAO;
import model.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.AbstractTestsWithTargets;
import services.DateUtils;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TaskMapperDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISlotDAO slotDAO;

    @Autowired
    IWeekDAO weekDAO;

    @Before
    public void init(){
        super.init();
    }

    @Test
    public void taskMapperByTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(taskMappersDAO.taskMapperForTask(task).getId()==taskMapper.getId());
    }

    @Test
    public void getFinishDateTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMapper.setFinishDate(DateUtils.currentDate());
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(DateUtils.fromDate(taskMappersDAO.finishDateByTaskid(task.getId())).equals(DateUtils.fromDate(DateUtils.currentDate())));
    }

    @Test
    public void getEmptyFinishDateTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        assertTrue(taskMappersDAO.finishDateByTaskid(task.getId())==null);
    }

    @Test
    public void byWeekAndDayTest(){
        SlotPosition slotPosition = new SlotPosition();
        slotPosition.setDaysOfWeek(DaysOfWeek.fri);
        slotDAO.saveOrUpdate(slotPosition);

        Week week = new Week();
        weekDAO.saveOrUpdate(week);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setSlotPosition(slotPosition);
        taskMapper.setWeek(week);
        taskMappersDAO.saveOrUpdate(taskMapper);

        List<TaskMapper> taskMappers = taskMappersDAO.byWeekAndDay(week, DaysOfWeek.fri);

        assertTrue(taskMappers.get(0).getId() == taskMapper.getId());

    }
}
