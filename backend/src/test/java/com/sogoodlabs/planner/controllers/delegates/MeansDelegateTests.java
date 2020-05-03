package com.sogoodlabs.planner.controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.dto.additional_mapping.AdditionalMeansMapping;
import com.sogoodlabs.planner.model.entities.*;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sogoodlabs.planner.services.QuarterGenerator;
import com.sogoodlabs.planner.services.StringUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;
import com.sogoodlabs.planner.test_configs.TestCreators;
import com.sogoodlabs.planner.test_configs.TestCreatorsAnotherSession;

import java.util.*;

import static org.junit.Assert.assertTrue;

@Transactional
public class MeansDelegateTests extends SpringTestConfig {

    @Autowired
    TestCreators testCreators;

    @Autowired
    TestCreatorsAnotherSession testCreators2;

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    MeansDelegate meansDelegate;

    @Autowired
    ITasksDAO tasksDAO;

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
    QuarterGenerator quarterGenerator;

    @Autowired
    IHQuarterDAO ihQuarterDAO;

    @Autowired
    IWeekDAO weekDAO;


    @Test
    public void updateTest(){

        Realm realm = testCreators2.createRealm();
        Mean mean = testCreators2.createMean(realm);
        Layer layer = testCreators2.createLayer(mean);
        Subject subject = testCreators2.createSubject(layer);
        Task task = testCreators2.createTask(subject);
        Topic topic = testCreators2.createTopic(task);
        TaskTesting taskTesting = testCreators2.createTesting(task);

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

        Realm realm = testCreators2.createRealm();
        Mean mean = testCreators2.createMean(realm);
        Layer layer = testCreators2.createLayer(mean);
        Subject subject = testCreators2.createSubject(layer);
        Task task = testCreators2.createTask(subject);
        Topic topic = testCreators2.createTopic(task);
        TaskTesting taskTesting = testCreators2.createTesting(task);

        Target target = testCreators2.createTarget(realm);

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
        Realm realm = testCreators2.createRealm();
        Mean oldMean = testCreators2.createMean(realm);

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
        Realm realm = testCreators2.createRealm();

        Target target = testCreators.createTarget(null, realm);

        Mean rootMean = testCreators.createMean( null, Arrays.asList(target), realm);

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
        Realm realm = testCreators2.createRealm();

        Target target = testCreators.createTarget(null, realm);

        Mean rootMean = testCreators.createMean( null, Arrays.asList(target), realm);

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
        Realm realm = testCreators2.createRealm();

        Mean meanRoot = testCreators.createMean(null, null, realm);
        Mean meanChild = testCreators.createMean(meanRoot,null, realm);

        Map<String, Object> meanRootMap = commonMapper.mapToDto(meanRoot);
        meanRootMap.put("nextid", null);

        Map<String, Object> meanChildMap = commonMapper.mapToDto(meanChild);
        meanChildMap.put("nextid", meanRoot.getId());
        meanChildMap.put("parentid", null);

        meansDelegate.reposition(Arrays.asList(meanRootMap, meanChildMap));
    }

    @Test
    public void deleteTest(){
        Realm realm = testCreators.createRealm();

        quarterGenerator.generateYear(2018);

        Target target = testCreators.createTarget(realm);

        Mean rootMean = testCreators.createMean(null, Arrays.asList(target), realm);
        Mean mean = testCreators.createMean(rootMean, Arrays.asList(target), realm);
        Mean meanChild = testCreators.createMean(mean, new ArrayList<>(), realm);
        Mean meanChildChild = testCreators.createMean(meanChild, new ArrayList<>(), realm);

        Layer layer = testCreators.createLayer(mean);
        Subject subject = testCreators.createSubject(layer);
        Task task = testCreators.createTask(subject);

        Slot slot = testCreators.createSlot(layer, mean, ihQuarterDAO.getHQuartersInYear(2018).get(5));
        SlotPosition slotPosition = testCreators.createSlotPosition(slot);

        TaskMapper taskMapper = testCreators.createTaskMapper(
                task, ihQuarterDAO.getHQuartersInYear(2018).get(5).getStartWeek(), slotPosition);

        Layer layer2 = testCreators.createLayer(meanChild);
        Layer layer3 = testCreators.createLayer(meanChildChild);

        meansDelegate.delete(mean.getId());

    }

}
