package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.DateUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class TasksDelegateTests extends SpringTestConfig {

    @PersistenceContext
    EntityManager entityManager;

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

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    Task task;
    TaskMapper taskMapper;
    RepetitionPlan defaultRepPlan;


    @Before
    public void init(){
        super.init();
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

        tasksDelegate.finishTaskWithRepetition(task.getId(), defaultRepPlan.getId(), null);
        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(task.getId());
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));

        assertTrue(spacedRepetitions!=null);
        assertTrue(spacedRepetitions.getRepetitionPlan().getId()== defaultRepPlan.getId());

        assertTrue(repetitions.size()==5);

        assertTrue(DateUtils.fromDate(repetitions.get(1).getPlanDate())
                .equals(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.currentDate(), 6))));
    }

    @Test
    public void finishTaskWithRepWithTestingsTest(){

        TaskTesting existingTesting = new TaskTesting();
        existingTesting.setTask(task);
        taskTestingDAO.save(existingTesting);

        List<Map<String, Object>> testingsDto = new ArrayList<>(Arrays.asList(
                createTaskTestingDTO(0, "testing1 q", null),
                createTaskTestingDTO(existingTesting.getId(), "testing2 q", task.getId())));

        entityManager.unwrap(Session.class).clear();

        tasksDelegate.finishTaskWithRepetition(task.getId(), defaultRepPlan.getId(), testingsDto);

        SpacedRepetitions spacedRepetitions = spacedRepDAO.getSRforTask(task.getId());
        List<Repetition> repetitions = repDAO.getRepsbySpacedRepId(spacedRepetitions.getId());

        assertTrue(DateUtils.fromDate(taskMappersDAO.taskMapperForTask(task).getFinishDate())
                .equals(DateUtils.fromDate(new Date(new java.util.Date().getTime()))));

        assertTrue(spacedRepetitions!=null);
        assertTrue(spacedRepetitions.getRepetitionPlan().getId()== defaultRepPlan.getId());

        assertTrue(repetitions.size()==5);

        assertTrue(DateUtils.fromDate(repetitions.get(1).getPlanDate())
                .equals(DateUtils.fromDate(DateUtils.addWeeks(DateUtils.currentDate(), 6))));

        List<TaskTesting> testings = taskTestingDAO.getByTask(task.getId());

        assertTrue(testings.size()==2);
        assertTrue(testings.get(0).getQuestion().equals("testing2 q"));
        assertTrue(testings.get(1).getQuestion().equals("testing1 q"));
    }

    @Test
    public void finishRepetitionTest(){
        Repetition repetition = new Repetition();
        repDAO.save(repetition);

        tasksDelegate.finishRepetition(repetition.getId());

        repetition = repDAO.findOne(repetition.getId());
        assertTrue(DateUtils.fromDate(repetition.getFactDate()).equals(DateUtils.currentDateString()));
    }

    @Test
    public void addNewTestingToTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        Map<String, Object> testingDto = new HashMap<>();
        testingDto.put("question", "test q");

        tasksDelegate.addNewTestingToTask(task.getId(), testingDto);

        List<TaskTesting> testings = taskTestingDAO.getByTask(task.getId());

        assertTrue(testings.size()==1);
        assertTrue(testings.get(0).getQuestion().equals("test q"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addExistingTestingToTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskTesting taskTesting = new TaskTesting();
        taskTestingDAO.save(taskTesting);

        Map<String, Object> testingDto = new HashMap<>();
        testingDto.put("id", taskTesting.getId());
        testingDto.put("question", "test q");

        tasksDelegate.addNewTestingToTask(task.getId(), testingDto);
    }

    private Map<String, Object> createTaskTestingDTO(long id, String question, Long taskid){
        Map<String, Object> result = new HashMap<>();
        if(id>0){
            result.put("id", id);
        }
        if(taskid!=null){
            result.put("taskid", taskid);
        }
        result.put("question", question);
        return result;
    }

}
