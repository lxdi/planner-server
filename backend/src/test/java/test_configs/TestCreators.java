package test_configs;

import model.dao.*;
import model.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCreators {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    SessionFactory sessionFactory;

    public Realm createRealm(){
        Realm realm = new Realm();
        save(realm);
        return realm;
    }

    public Target createTarget(Realm realm){
        Target target = new Target();
        target.setRealm(realm);
        save(target);
        return target;
    }

    public Mean createMean(Realm realm){
        Mean mean = new Mean();
        mean.setRealm(realm);
        save(mean);
        return mean;
    }

    public Layer createLayer(Mean mean){
        Layer layer = new Layer();
        layer.setMean(mean);
        save(layer);
        return layer;
    }

    public Subject createSubject(Layer layer){
        Subject subject =  new Subject();
        subject.setLayer(layer);
        save(subject);
        return subject;
    }

    public Task createTask(Subject subject){
        Task task =  new Task();
        task.setSubject(subject);
        save(task);
        return task;
    }

    public Topic createTopic(Task task){
        Topic topic = new Topic();
        topic.setTask(task);
        save(topic);
        return topic;
    }

    public TaskTesting createTesting(Task task){
        TaskTesting taskTesting = new TaskTesting();
        taskTesting.setTask(task);
        save(taskTesting);
        return taskTesting;
    }

    private void save(Object object){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
        session.close();
    }


}
