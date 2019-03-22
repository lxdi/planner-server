package controllers.delegates;

import model.dao.*;
import model.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import services.DateUtils;
import test_configs.SpringTestConfig;

import java.sql.Date;
import java.util.List;

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

    @Autowired
    IRepDAO repDAO;

    Task task;
    TaskMapper taskMapper;
    RepetitionPlan defaultRepPlan;

    @Before
    public void init(){
        task = new Task();
        tasksDAO.saveOrUpdate(task);

        taskMapper = new TaskMapper();
        taskMapper.setTask(task);
        taskMappersDAO.saveOrUpdate(taskMapper);

        defaultRepPlan = repPlanDAO.getAll().get(0);
    }

    @Test
    public void finishTaskTest(){

        tasksDelegate.finishTask(task.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));
    }

    @Test
    public void finishTaskWithRepTest(){

        tasksDelegate.finishTaskWithRepetition(task.getId(), defaultRepPlan.getId());
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(task.getId());
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));

        assertTrue(spacedRepetitions!=null);
        assertTrue(spacedRepetitions.getRepetitionPlan().getId()== defaultRepPlan.getId());

        assertTrue(repetitions.size()==4);

        assertTrue(DateUtils.fromDate(repetitions.get(0).getPlanDate())
                .equals(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.currentDate(), 6))));
    }

    @Test
    public void finishRepetitionTest(){
        Repetition repetition = new Repetition();
        repDAO.save(repetition);

        tasksDelegate.finishRepetition(repetition.getId());

        repetition = repDAO.findOne(repetition.getId());
        assertTrue(DateUtils.fromDate(repetition.getFactDate()).equals(DateUtils.currentDateString()));
    }

}
