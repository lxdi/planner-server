package model.dto.task;

import model.dao.*;
import model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test_configs.SpringTestConfig;

import static org.junit.Assert.assertTrue;

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

    @Test
    public void mapToDtoTest(){
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

        TaskTesting taskTesting1 = new TaskTesting();
        taskTesting1.setQuestion("test question");
        taskTesting1.setTask(task);
        taskTestingDAO.save(taskTesting1);

        TaskDtoLazy result = tasksDtoMapper.mapToDto(task);

        assertTrue(result.getTestings().size()==1);
        assertTrue((long)result.getTestings().get(0).get("id")==taskTesting1.getId());
        assertTrue(result.getTestings().get(0).get("question").equals("test question"));
        assertTrue(result.getFullname().equals("realm/mean/Layer-1/subject/task"));

    }

}
