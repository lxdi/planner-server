package orm_tests;

import model.dao.ILayerDAO;
import model.dao.IMeansDAO;
import model.dao.ISubjectDAO;
import model.dao.ITasksDAO;
import model.entities.Layer;
import model.entities.Mean;
import model.entities.Subject;
import model.entities.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import orm_tests.conf.AbstractTestsWithTargets;

import static junit.framework.TestCase.assertTrue;

public class DeletingMeanAndDependingsTest extends AbstractTestsWithTargets {

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ILayerDAO layerDAO;

    @Autowired
    ISubjectDAO subjectDAO;

    @Autowired
    ITasksDAO tasksDAO;

    Mean mean;
    Layer layer;
    Subject subject;
    Task task;

    @Before
    public void init(){
        super.init();
        mean = new Mean("test mean", realm);
        meansDAO.saveOrUpdate(mean);

        layer = new Layer();
        layer.setPriority(1);
        layer.setMean(mean);
        layerDAO.saveOrUpdate(layer);

        subject = new Subject();
        subject.setPosition(1);
        subject.setLayer(layer);
        subjectDAO.saveOrUpdate(subject);

        task = new Task();
        task.setPosition(1);
        task.setSubject(subject);
        tasksDAO.saveOrUpdate(task);

    }

    @Test
    public void deleteMeanTest(){
        meansDAO.deleteMean(mean.getId());

        assertTrue(meansDAO.meanById(mean.getId())==null);
        assertTrue(layerDAO.layerById(layer.getId())==null);
        assertTrue(subjectDAO.getById(subject.getId())==null);
        assertTrue(tasksDAO.getById(task.getId())==null);


    }


}
