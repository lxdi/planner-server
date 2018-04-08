package orm_tests.conf;

import configuration.HibernateConfigMain;
import model.ITargetsDAO;
import model.MeansDao;
import model.TargetsDao;
import model.dto.mean.MeansDtoMapper;
import model.dto.target.TargetsDtoMapper;
import model.entities.Target;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 07.04.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfigMain.class, EmbeddedDBConf.class, TargetsDao.class, TargetsDtoMapper.class, MeansDao.class, MeansDtoMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractTestsWithTargets {

    @Autowired
    protected ITargetsDAO targetsDAO;

    protected String defaultParentTarget = "drefault parent";
    protected String defaultChildTarget = "default child";
    protected String defaultChild2Target = "default child2";
    protected String defaultChildChildTarget = "default child child";

    @Before
    public void init(){
        Target parentTarget = new Target(defaultParentTarget);
        targetsDAO.saveOrUpdate(parentTarget);
        Target target = new Target(defaultChildTarget);
        Target target2 = new Target(defaultChild2Target);
        Target target3 = new Target(defaultChildChildTarget);
        target.setParent(parentTarget);
        target2.setParent(parentTarget);
        target3.setParent(target2);
        targetsDAO.saveOrUpdate(target);
        targetsDAO.saveOrUpdate(target2);
        targetsDAO.saveOrUpdate(target3);

        assertTrue(parentTarget.getId()==1);
        assertTrue(target.getId()==2);
        assertTrue(target2.getId()==3);
        assertTrue(target3.getId()==4);
    }

}
