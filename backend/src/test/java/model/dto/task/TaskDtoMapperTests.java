package model.dto.task;

import model.dao.*;
import model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.SpringTestConfig;

import static org.junit.Assert.assertTrue;


@Transactional
public class TaskDtoMapperTests extends SpringTestConfig {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    TasksDtoMapper tasksDtoMapper;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void mapToDtoTest(){

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Realm realm = new Realm();
        realm.setTitle("realm");
        session.saveOrUpdate(realm);

        Mean mean = new Mean();
        mean.setTitle("mean");
        mean.setRealm(realm);
        session.saveOrUpdate(mean);

        Layer layer = new Layer();
        layer.setPriority(1);
        layer.setMean(mean);
        session.saveOrUpdate(layer);

        Subject subject = new Subject();
        subject.setTitle("subject");
        subject.setLayer(layer);
        session.saveOrUpdate(subject);

        Task task = new Task();
        task.setTitle("task");
        task.setSubject(subject);
        session.saveOrUpdate(task);

        TaskTesting taskTesting1 = new TaskTesting();
        taskTesting1.setQuestion("test question");
        taskTesting1.setTask(task);
        session.save(taskTesting1);

        transaction.commit();
        session.close();

        TaskDtoLazy result = tasksDtoMapper.mapToDto(tasksDAO.getById(task.getId()));

        assertTrue(result.getTestings().size()==1);
        assertTrue((long)result.getTestings().get(0).get("id")==taskTesting1.getId());
        assertTrue(result.getTestings().get(0).get("question").equals("test question"));
        assertTrue(result.getFullname().equals("realm/mean/Layer-1/subject/task"));

    }

}
