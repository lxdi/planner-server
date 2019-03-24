package model.dto.additional_mapping;

import model.dao.*;
import model.dto.task.TasksDtoMapper;
import model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.SpringTestConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class AdditionalTasksMappingTests extends SpringTestConfig {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    AdditionalTasksMapping additionalTasksMapping;

    @Test
    public void fillFullnameTest(){
        Realm realm = new Realm();
        realm.setTitle("realm");
        realmDAO.saveOrUpdate(realm);

        Mean mean = new Mean();
        mean.setTitle("mean");
        mean.setRealm(realm);
        meansDAO.saveOrUpdate(mean);

        Layer layer = new Layer();
        layer.setPriority(1);
        layer.setMean(mean);
        layerDAO.saveOrUpdate(layer);

        Subject subject = new Subject();
        subject.setTitle("subject");
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);

        Task task = new Task();
        task.setTitle("task");
        task.setSubject(subject);
        tasksDAO.saveOrUpdate(task);

//        TaskTesting taskTesting1 = new TaskTesting();
//        taskTesting1.setQuestion("test question");
//        taskTesting1.setTask(task);
//        taskTestingDAO.save(taskTesting1);

        Map<String, Object> result = new HashMap<>();
        additionalTasksMapping.fillFullName(result, task);

        assertTrue(result.get("fullname").equals("realm/mean/Layer-1/subject/task"));

    }

    @Test
    public void fillTopicsTest(){
        Task task = new Task();
        tasksDAO.saveOrUpdate(task);

        Topic topic = new Topic();
        topic.setTask(task);
        topic.setTitle("topic");
        topicDAO.saveOrUpdate(topic);

        Topic topic2 = new Topic();
        topic2.setTask(task);
        topic2.setTitle("topic2");
        topicDAO.saveOrUpdate(topic2);

        Map<String, Object> result = new HashMap<>();
        result.put("id", task.getId());

        additionalTasksMapping.fillTopicsInTaskDto(result);

        assertTrue(((List)result.get("topics")).size()==2);
        assertTrue(((Map<String,Object>)((List)result.get("topics")).get(0)).get("title").equals("topic"));
        assertTrue(((Map<String,Object>)((List)result.get("topics")).get(1)).get("title").equals("topic2"));


    }

}
