package model.dao;

import model.dao.*;
import model.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.AbstractTestsWithTargets;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

@Transactional
public class TasksDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskMappersDAO taskMappersDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    Task task;

    Topic topic;
    Topic topic2;

    @Override
    @Before
    public void init(){
        super.init();
    }

    @Test
    public void tasksByLayerTest(){
        Layer layer0 = new Layer();
        layerDAO.saveOrUpdate(layer0);

        Layer layer = new Layer();
        layerDAO.saveOrUpdate(layer);

        Subject subject =  new Subject();
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);

        Task task1 = new Task();
        task1.setSubject(subject);
        tasksDAO.saveOrUpdate(task1);

        Task task2 = new Task();
        task2.setSubject(subject);
        tasksDAO.saveOrUpdate(task2);

        Task task3 = new Task();
        tasksDAO.saveOrUpdate(task3);

        List<Task> tasks = tasksDAO.tasksByLayer(layer);

        assertTrue(tasksDAO.tasksByLayer(layer0).size()==0);

        assertTrue(tasks.size()==2);
        assertTrue(tasks.get(0).getId()==task1.getId());
        assertTrue(tasks.get(1).getId()==task2.getId());

    }

    @Test
    public void gettingTopicsAlongWithTasks(){
        createTestData();

        Task taskLoaded = tasksDAO.getById(task.getId());

        assertTrue(taskLoaded.getTopics().size()==2);
        assertTrue(((Topic)taskLoaded.getTopics().toArray()[0]).getTask()==taskLoaded);

    }

    @Test
    public void deletingTaskWithTopics(){
        createTestData();

        tasksDAO.delete(task.getId());

        assertTrue(topicDAO.getById(topic.getId())==null);
        assertTrue(topicDAO.getById(topic2.getId())==null);

    }

    @Test
    public void updatingTopics(){
        createTestData();

        Iterator<Topic> iterator = task.getTopics().iterator();
        iterator.next();
        iterator.remove();
        iterator.next().setTitle("title changed");
        tasksDAO.saveOrUpdate(task);

        Task taskLoaded = tasksDAO.getById(task.getId());

        assertTrue(taskLoaded.getTopics().size()==1);
        assertTrue(((Topic)taskLoaded.getTopics().toArray()[0]).getTitle().equals("title changed"));
        assertTrue(topicDAO.getById(topic.getId())==null || topicDAO.getById(topic2.getId())==null);

    }

    @Test
    public void updating2Topics(){
        createTestData();

        Set<Topic> topics = new HashSet<>();
        task.getTopics().forEach(topics::add);
        Iterator<Topic> iterator = topics.iterator();
        iterator.next();
        iterator.remove();
        //topics.remove(0);

        task.setTopics(topics);
        tasksDAO.saveOrUpdate(task);

        Task taskLoaded = tasksDAO.getById(task.getId());

        assertTrue(taskLoaded.getTopics().size()==1);
        assertTrue(topicDAO.getById(topic.getId())==null || topicDAO.getById(topic2.getId())==null);

    }

    @Test
    public void updating3Topics(){
        createTestData();
        createTestData();

        Set<Topic> topics = new HashSet<>();
        task.getTopics().forEach(topics::add);
        Iterator<Topic> iterator = topics.iterator();
        iterator.next();
        iterator.remove();
        //topics.remove(0);

        task.setTopics(topics);
        tasksDAO.saveOrUpdate(task);

        Task taskLoaded = tasksDAO.getById(task.getId());

        assertTrue(taskLoaded.getTopics().size()==1);
        assertTrue(topicDAO.getById(topic.getId())==null || topicDAO.getById(topic2.getId())==null);

    }

    @Test
    public void deletingTaskTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        TaskTesting taskTesting = new TaskTesting();
        taskTesting.setTask(task);
        taskTestingDAO.save(taskTesting);

        tasksDAO.delete(task.getId());

        assertTrue(tasksDAO.getById(task.getId())==null);
        assertTrue(taskTestingDAO.findOne(taskTesting.getId())==null);
    }

    private void createTestData(){
        Mean mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

        Layer layer = new Layer(mean, 1);
        layerDAO.saveOrUpdate(layer);

        Subject subject = new Subject(layer, 1);
        subjectDAO.saveOrUpdate(subject);

        topic = new Topic("test topic 1", null);
        //topicDAO.saveOrUpdate(topic);
        topic2 = new Topic("test topic 2", null);
        //topicDAO.saveOrUpdate(topic2);

        task = new Task("test task1", subject, 1);
        topic.setTask(task);
        topic2.setTask(task);
        task.getTopics().addAll(new ArrayList<>(Arrays.asList(topic, topic2)));

        tasksDAO.saveOrUpdate(task);

    }

}
