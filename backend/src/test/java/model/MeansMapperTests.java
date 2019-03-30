package model;

import model.dao.IMeansDAO;
import model.dao.IRealmDAO;
import model.dto.MeansMapper;
import model.entities.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import services.StringUtils;
import test_configs.SpringTestConfig;
import test_configs.TestCreators;

import java.util.Map;

import static org.junit.Assert.assertTrue;

@Transactional
public class MeansMapperTests extends SpringTestConfig {

    @Autowired
    TestCreators testCreators;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    MeansMapper meansMapper;

    @Test
    public void mapToDtoLazyTest(){

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

        Map<String, Object> result = meansMapper.mapToDtoLazy(mean);

        assertTrue(result.get("targetsIds")!=null);
        assertTrue(((long) StringUtils.getValue(result, "get('targetsIds').get(0)")==target.getId()));
        assertTrue(result.get("layers")==null);

    }

    @Test
    public void mapToDtoFullTest(){

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

        Map<String, Object> result = meansMapper.mapToDtoFull(mean);

        assertTrue(result.get("targetsIds")!=null);
        assertTrue(((long) StringUtils.getValue(result, "get('targetsIds').get(0)")==target.getId()));
        assertTrue(result.get("layers")!=null);
        assertTrue(((long) StringUtils.getValue(result, "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('id')")
                ==task.getId()));

        assertTrue(!((boolean) StringUtils.getValue(result, "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('finished')")));

        assertTrue(((long) StringUtils.getValue(result,
                "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('topics').get(0).get('id')")
                ==topic.getId()));

        assertTrue(((long) StringUtils.getValue(result,
                "get('layers').get(0).get('subjects').get(0).get('tasks').get(0).get('testings').get(0).get('id')")
                ==taskTesting.getId()));

        assertTrue(result.containsKey("nextid"));

    }



}
