package orm_tests;

import model.dao.ITasksDAO;
import model.dao.ITopicDAO;
import model.entities.Task;
import model.entities.Topic;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.SpringTestConfig;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TopicDaoTests extends SpringTestConfig {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Test
    public void getByTaskidTest(){
        Task task1 = new Task();
        tasksDAO.saveOrUpdate(task1);

        Task task2 = new Task();
        tasksDAO.saveOrUpdate(task2);

        Topic topic1 = new Topic();
        topic1.setTask(task1);
        topicDAO.saveOrUpdate(topic1);

        Topic topic2 = new Topic();
        topic2.setTask(task1);
        topicDAO.saveOrUpdate(topic2);

        Topic topic3 = new Topic();
        topic3.setTask(task2);
        topicDAO.saveOrUpdate(topic3);

        List<Topic> topicsOfTask1 = topicDAO.getByTaskId(task1.getId());
        assertTrue(topicsOfTask1.size()==2);
        assertTrue(topicsOfTask1.get(0).getId()==topic1.getId());
        assertTrue(topicsOfTask1.get(1).getId()==topic2.getId());

        List<Topic> topicsOfTask2 = topicDAO.getByTaskId(task2.getId());
        assertTrue(topicsOfTask2.size()==1);
        assertTrue(topicsOfTask2.get(0).getId()==topic3.getId());
    }

}
