package controllers.delegates;

import model.dao.IRepPlanDAO;
import model.dao.ISpacedRepDAO;
import model.dao.ITaskMappersDAO;
import model.dao.ITasksDAO;
import model.entities.RepetitionPlan;
import model.entities.SpacedRepetitions;
import model.entities.Task;
import model.entities.TaskMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import test_configs.SpringTestConfig;

import java.sql.Date;

import static junit.framework.TestCase.assertTrue;

public class TasksDelegateTests extends SpringTestConfig {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    TasksDelegate tasksDelegate;

    @Autowired
    IRepPlanDAO repPlanDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    Task task;
    TaskMapper taskMapper;
    RepetitionPlan repPlan;

    @Before
    public void init(){
        task = new Task();
        tasksDAO.saveOrUpdate(task);

        taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        repPlan = new RepetitionPlan();
        repPlanDAO.save(repPlan);
    }

    @Test
    public void finishTaskTest(){

        tasksDelegate.finishTask(task.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));
    }

    @Test
    public void finishTaskWithRepTest(){

        tasksDelegate.finishTaskWithRepetition(task.getId(), repPlan.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));

        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(task.getId());
        assertTrue(spacedRepetitions!=null);
        assertTrue(spacedRepetitions.getRepetitionPlan().getId()==repPlan.getId());
    }

}
