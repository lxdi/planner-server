package orm_tests;

import model.dao.ILayerDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.entities.Layer;
import model.entities.Subject;
import model.entities.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TasksDaoTests extends AbstractTestsWithTargets {

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

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

}
