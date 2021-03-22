package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.model.dao.ITaskTestingDAO;
import com.sogoodlabs.planner.model.dao.ITasksDAO;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.TaskTesting;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.Assert.assertEquals;

@Transactional
public class TaskTestingsUpdateServiceTest extends SpringTestConfig {

    @Autowired
    private ITasksDAO tasksDAO;

    @Autowired
    private ITaskTestingDAO taskTestingDAO;

    @Autowired
    private TaskTestingsUpdateService taskTestingsUpdateService;

    @Test
    public void updateTest(){
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        tasksDAO.save(task);

        TaskTesting testingRoot1 = new TaskTesting();
        testingRoot1.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "1");
        testingRoot1.setQuestion("question1");

        TaskTesting testingRoot2 = new TaskTesting();
        testingRoot2.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "2");
        testingRoot2.setQuestion("question2");

        TaskTesting testingChild1 = new TaskTesting();
        testingChild1.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "3");
        testingChild1.setQuestion("question3");

        TaskTesting testingChild2 = new TaskTesting();
        testingChild2.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "4");
        testingChild2.setQuestion("question4");

        TaskTesting testingDummyRoot1 = new TaskTesting();
        testingDummyRoot1.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "1");
        testingChild1.setParent(testingDummyRoot1);
        testingChild2.setParent(testingDummyRoot1);

        TaskTesting testingDummyRoot2 = new TaskTesting();
        testingDummyRoot2.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "2");
        testingRoot1.setNext(testingDummyRoot2);

        TaskTesting testingDummyChild2 = new TaskTesting();
        testingDummyChild2.setId(TaskTestingsUpdateService.NEW_ID_PREFIX + "4");
        testingChild1.setNext(testingDummyChild2);

        taskTestingsUpdateService.update(task, Arrays.asList(testingRoot1, testingRoot2, testingChild1, testingChild2));

        super.cleanContext();

        List<TaskTesting> testingList = taskTestingDAO.findByTask(task);
        Map<String, TaskTesting> questionToTesting = new HashMap<>();
        testingList.forEach(testing -> questionToTesting.put(testing.getQuestion(), testing));

        assertEquals(4, testingList.size());
        assertEquals("question2", questionToTesting.get("question1").getNext().getQuestion());
        assertEquals("question4", questionToTesting.get("question3").getNext().getQuestion());
        assertEquals("question1", questionToTesting.get("question3").getParent().getQuestion());
        assertEquals("question1", questionToTesting.get("question4").getParent().getQuestion());
    }

}
