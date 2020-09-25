package com.sogoodlabs.planner.model.dto.additional_mapping;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@Transactional
public class AdditionalTasksMappingTests extends SpringTestConfig {

    @PersistenceContext
    EntityManager entityManager;

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
    AdditionalTasksMapping additionalTasksMapping;

    @Test
    public void fillFullnameTest(){
        Realm realm = new Realm();
        realm.setTitle("realm");
        realmDAO.saveOrUpdate(realm);

        Mean mean = new Mean();
        mean.setTitle("mean");
        mean.setRealm(realm);
        meansDAO.save(mean);

        Layer layer = new Layer();
        layer.setPriority(1);
        layer.setMean(mean);
        layerDAO.saveOrUpdate(layer);

        Subject subject = new Subject();
        subject.setTitle("subject");
        subject.setLayer(layer);
        subjectDAO.save(subject);

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

        Session session = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).openSession();
        Transaction transaction = session.beginTransaction();

        Task task = new Task();
        session.saveOrUpdate(task);
        //tasksDAO.saveOrUpdate(task);

        Topic topic = new Topic();
        topic.setTask(task);
        topic.setTitle("topic");
        session.saveOrUpdate(topic);

        Topic topic2 = new Topic();
        topic2.setTask(task);
        topic2.setTitle("topic2");
        session.saveOrUpdate(topic2);

        transaction.commit();
        session.close();

        Map<String, Object> result = new HashMap<>();
        result.put("id", task.getId());

        additionalTasksMapping.fillTopicsInTaskDto(result, tasksDAO.getById(task.getId()));

        assertTrue(((List)result.get("topics")).size()==2);
        List<Map<String, Object>> topicsDto = (List)result.get("topics");
        topicsDto.sort(Comparator.comparingLong(dto -> (long) dto.get("id")));
        assertTrue(((Map<String,Object>)((List)result.get("topics")).get(0)).get("title").equals("topic"));
        assertTrue(((Map<String,Object>)((List)result.get("topics")).get(1)).get("title").equals("topic2"));


    }

}
