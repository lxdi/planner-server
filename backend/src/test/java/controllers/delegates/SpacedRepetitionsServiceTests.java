package controllers.delegates;

import model.dao.*;
import model.dto.task.TaskDtoLazy;
import model.entities.Repetition;
import model.entities.SpacedRepetitions;
import model.entities.Task;
import model.entities.TaskMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import test_configs.SpringTestConfig;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SpacedRepetitionsServiceTests extends SpringTestConfig {

    @Autowired
    IRepDAO repDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    SpacedRepetitionsService spacedRepetitionsService;

    @Test
    public void getActualTaskToRepeatTest(){

        Task task1 = initEntChain(DateUtils.currentDate());
        Task task2 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 2));
        Task task3 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -1));

        Task task4 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), -5));

        Task task5 = initEntChain(DateUtils.addDays(DateUtils.currentDate(), 6));

        Repetition repetitionDone = new Repetition();
        repetitionDone.setPlanDate(DateUtils.addDays(DateUtils.currentDate(), 3));
        repetitionDone.setFactDate(DateUtils.addDays(DateUtils.currentDate(), 4));
        repDAO.save(repetitionDone);

        Map<Integer, List<TaskDtoLazy>> tasks = spacedRepetitionsService.getActualTaskToRepeat();

        assertTrue(tasks.get(-1).size()==1);
        assertTrue(tasks.get(-1).get(0).getId()==task4.getId());

        assertTrue(tasks.get(0).size()==3);
        assertTrue(tasks.get(0).get(0).getId()==task3.getId());
        assertTrue(tasks.get(0).get(1).getId()==task1.getId());
        assertTrue(tasks.get(0).get(2).getId()==task2.getId());

        assertTrue(tasks.get(1).size()==1);
        assertTrue(tasks.get(1).get(0).getId()==task5.getId());
    }

    private Task initEntChain(Date planDate){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskMapper taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        SpacedRepetitions spacedRepetitions = new SpacedRepetitions();
        spacedRepetitions.setTaskMapper(taskMapper);
        spacedRepDAO.save(spacedRepetitions);

        Repetition repetition = new Repetition();
        repetition.setSpacedRepetitions(spacedRepetitions);
        repetition.setPlanDate(planDate);
        repDAO.save(repetition);

        return task;
    }

}
