package model.entities;

import model.dao.ITaskTestingDAO;
import model.dao.ITasksDAO;
import model.dao.ITopicDAO;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.SpringTestConfig;
import test_configs.TestCreatorsAnotherSession;

import static org.junit.Assert.assertTrue;

@Transactional
public class TopicsTestingsInTaskTests extends SpringTestConfig {

    @Autowired
    TestCreatorsAnotherSession testCreators;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Test
    @Ignore
    public void deletingTest(){
//        Task task = testCreators.createTask(null);
//
//        Topic topic = testCreators.createTopic(task);
//        TaskTesting testing = testCreators.createTesting(task);
//
//        task = tasksDAO.getById(task.getId());
//
//        assertTrue(task.getTopics().size()==1);
//        assertTrue(task.getTestings().size()==1);
//
//        task.getTopics().clear();
//        task.getTestings().clear();
//
//        tasksDAO.saveOrUpdate(task);
//
//        assertTrue(topicDAO.getById(topic.getId())==null);
//        assertTrue(taskTestingDAO.findOne(testing.getId())==null);

    }

}
