package orm_tests.conf;

import configuration.HibernateConfigMain;
import model.dao.*;
import model.dto.hquarter.HquarterMapper;
import model.dto.layer.LayersDtoMapper;
import model.dto.mean.MeansDtoMapper;
import model.dto.slot.SlotMapper;
import model.dto.slot.SlotPositionMapper;
import model.dto.subject.SubjectDtoMapper;
import model.dto.target.TargetsDtoMapper;
import model.dto.task.TasksDtoMapper;
import model.entities.Realm;
import model.entities.Target;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import services.QuarterGenerator;
import services.WeeksGenerator;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 07.04.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
        classes = {HibernateConfigMain.class, EmbeddedDBConf.class,
                RealmDao.class, TargetsDao.class, TargetsDtoMapper.class,
                MeansDao.class, MeansDtoMapper.class,
                WeekDao.class, WeeksGenerator.class,
                TasksDao.class, TasksDtoMapper.class,
                HQuarterDao.class, HquarterMapper.class,  QuarterGenerator.class,
                LayerDao.class, LayersDtoMapper.class,
                SubjectDao.class, SubjectDtoMapper.class,
                SlotDao.class, SlotMapper.class,
                SlotPositionMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractTestsWithTargets {

    @Autowired
    protected IRealmDAO realmDAO;

    @Autowired
    protected ITargetsDAO targetsDAO;

    protected Realm realm;

    protected String defaultParentTarget = "drefault parent";
    protected String defaultChildTarget = "default child";
    protected String defaultChild2Target = "default child2";
    protected String defaultChildChildTarget = "default child child";

    @Before
    public void init(){
        realm = realmDAO.createRealm("test realm");

        Target parentTarget = new Target(defaultParentTarget, realm);
        targetsDAO.saveOrUpdate(parentTarget);
        Target target = new Target(defaultChildTarget, realm);
        Target target2 = new Target(defaultChild2Target, realm);
        Target target3 = new Target(defaultChildChildTarget, realm);
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
