package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.*;
import model.dto.additional_mapping.AdditionalMeansMapping;
import model.entities.*;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import services.StringUtils;
import test_configs.SpringTestConfig;
import test_configs.TestCreators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Transactional
public class MeansDelegateTests extends SpringTestConfig {

    @Autowired
    TestCreators testCreators;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    MeansDelegate meansDelegate;

    @Autowired
    ITasksDAO tasksDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    AdditionalMeansMapping additionalMeansMapping;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    ITopicDAO topicDAO;

    @Autowired
    ITaskTestingDAO taskTestingDAO;

    @Autowired
    ITargetsDAO targetsDAO;


    @Test
    public void updateTest(){

        Realm realm = testCreators.createRealm();
        Mean mean = testCreators.createMean(realm);
        Layer layer = testCreators.createLayer(mean);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);
        Topic topic = testCreators.createTopic(task);
        TaskTesting taskTesting = testCreators.createTesting(task);

        Map<String, Object> dto = commonMapper.mapToDto(mean);
        additionalMeansMapping.mapLayers(mean, dto);
        dto.put("title", "mean changed");
        ((Map<String, Object>)StringUtils.getValue(dto, "get('layers').get(0).get('subjects').get(0).get('tasks').get(0)"))
                .put("title", "task changed");

        ((Map<String, Object>)StringUtils.getValue(dto,
                "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('topics').get(0)"))
                .put("title", "topic changed");

        ((Map<String, Object>)StringUtils.getValue(dto,
                "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('testings').get(0)"))
                .put("question", "testing changed");

        sessionFactory.getCurrentSession().clear();

        meansDelegate.update(dto);

        assertTrue(meansDAO.meanById(mean.getId()).getTitle().equals("mean changed"));
        assertTrue(tasksDAO.getById(task.getId()).getTitle().equals("task changed"));
        assertTrue(topicDAO.getById(topic.getId()).getTitle().equals("topic changed"));
        assertTrue(taskTestingDAO.findOne(taskTesting.getId()).getQuestion().equals("testing changed"));
    }

    @Test
    public void getAllMeansTest(){

        Realm realm = testCreators.createRealm();
        Mean mean = testCreators.createMean(realm);
        Layer layer = testCreators.createLayer(mean);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);
        Topic topic = testCreators.createTopic(task);
        TaskTesting taskTesting = testCreators.createTesting(task);

        Target target = testCreators.createTarget(realm);

        mean.getTargets().add(target);
        meansDAO.saveOrUpdate(mean);

        assertTrue(meansDAO.meanById(mean.getId()).getTargets().get(0).getId()==target.getId());

        List<Map<String, Object>> result = meansDelegate.getAllMeans();

        assertTrue(result.size()==1);
        assertTrue(result.get(0).get("targetsIds")!=null);
        assertTrue(((long)StringUtils.getValue(result, "get(0).get('targetsIds').get(0)")==target.getId()));
        assertTrue(result.get(0).containsKey("nextid"));
    }

    @Test
    public void createTest(){
        Realm realm = testCreators.createRealm();
        Mean oldMean = testCreators.createMean(realm);

        Map<String, Object> dto = new HashMap<>();
        dto.put("title", "new mean");
        dto.put("realmid", realm.getId());

        Map<String, Object> result = meansDelegate.create(dto);

        assertTrue((long)result.get("id")>0);
        assertTrue(result.get("title").equals("new mean"));
        assertTrue((long)result.get("previd")==oldMean.getId());
    }

    @Test
    public void createChildTest(){
        Realm realm = testCreators.createRealm();

        Target target = createTestTarget(null, realm);

        Mean rootMean = createMean(Arrays.asList(target), null, realm);

        Mean newMean = new Mean();
        newMean.setParent(rootMean);
        newMean.setRealm(realm);

        Map<String, Object> newMeanMap = commonMapper.mapToDto(newMean);

        assertTrue(meansDAO.meanById(rootMean.getId()).getTargets().size()==1);

        Map<String, Object> result = meansDelegate.create(newMeanMap);

        newMean = meansDAO.meanById((Long) result.get("id"));
        assertTrue(meansDAO.meanById(rootMean.getId()).getTargets().size()==0);
        assertTrue(newMean.getTargets().size()==1);

    }

    @Test
    public void createChildTest2(){
        Realm realm = testCreators.createRealm();

        Target target = createTestTarget(null, realm);

        Mean rootMean = createMean(Arrays.asList(target), null, realm);

        Mean newMean = new Mean();
        newMean.setParent(rootMean);
        newMean.setRealm(realm);
        newMean.setTargets(Arrays.asList(target));

        Map<String, Object> newMeanMap = commonMapper.mapToDto(newMean);
        additionalMeansMapping.mapTargetsIdsToDto(newMean, newMeanMap);

        assertTrue(meansDAO.meanById(rootMean.getId()).getTargets().size()==1);

        Map<String, Object> result = meansDelegate.create(newMeanMap);

        newMean = meansDAO.meanById((Long) result.get("id"));
        assertTrue(meansDAO.meanById(rootMean.getId()).getTargets().size()==0);
        assertTrue(newMean.getTargets().size()==1);

    }

    @Test
    public void repositionWhenParentBecomesNextTest(){
        Realm realm = testCreators.createRealm();

        Mean meanRoot = createMean(null, null, realm);
        Mean meanChild = createMean(null, meanRoot, realm);

        Map<String, Object> meanRootMap = commonMapper.mapToDto(meanRoot);
        meanRootMap.put("nextid", null);

        Map<String, Object> meanChildMap = commonMapper.mapToDto(meanChild);
        meanChildMap.put("nextid", meanRoot.getId());
        meanChildMap.put("parentid", null);

        meansDelegate.reposition(Arrays.asList(meanRootMap, meanChildMap));
    }

    private Target createTestTarget(Target parent, Realm realm){
        Target target = new Target();
        target.setRealm(realm);
        target.setParent(parent);
        targetsDAO.saveOrUpdate(target);
        return target;
    }


    private Mean createMean(List<Target> targets, Mean parent, Realm realm){
        Mean mean = new Mean();
        mean.setParent(parent);
        mean.setTargets(targets);
        mean.setRealm(realm);
        meansDAO.saveOrUpdate(mean);
        return mean;
    }


}
