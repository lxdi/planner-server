package controllers.delegates;

import com.sogoodlabs.common_mapper.CommonMapper;
import model.dao.IMeansDAO;
import model.dao.IRealmDAO;
import model.dao.ITargetsDAO;
import model.entities.Mean;
import model.entities.Realm;
import model.entities.Target;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import test_configs.SpringTestConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@Transactional
public class TargetsDelegateTests extends SpringTestConfig {

    @Autowired
    ITargetsDAO targetsDAO;

    @Autowired
    IMeansDAO meansDAO;

    @Autowired
    IRealmDAO realmDAO;

    @Autowired
    TargetsDelegate targetsDelegate;

    @Autowired
    CommonMapper commonMapper;

    Realm realm;

    @Before
    public void init(){
        realm = new Realm();
        realmDAO.saveOrUpdate(realm);
    }

    @Test
    public void createTargetAsChildTest(){
        Target target = createTestTarget(null);

        Mean mean1 = createMean(Arrays.asList(target), null);
        Mean mean2 = createMean(Arrays.asList(target), null);

        Target newTarget= new Target();
        newTarget.setRealm(realm);
        newTarget.setParent(target);

        Map<String, Object> newTargetMap = commonMapper.mapToDto(newTarget);

        assertTrue(meansDAO.meansAssignedToTarget(target).size()==2);

        Map<String, Object> result = targetsDelegate.createTarget(newTargetMap);

        newTarget = targetsDAO.targetById((Long) result.get("id"));
        assertTrue(meansDAO.meansAssignedToTarget(target).size()==0);
        assertTrue(meansDAO.meansAssignedToTarget(newTarget).size()==2);

    }

    @Test
    public void updateListWhenParentBecomesNextTest(){

        Target rootTarget = createTestTarget(null);
        Target childTarget = createTestTarget(rootTarget);

        Map<String, Object> rootTargetDto = commonMapper.mapToDto(rootTarget);
        rootTargetDto.put("nextid", null);

        Map<String, Object> childTargetDto = commonMapper.mapToDto(childTarget);
        childTargetDto.put("parentid", null);
        childTargetDto.put("nextid", rootTarget.getId());

        List<Map<String, Object>> result = targetsDelegate.updateList(Arrays.asList(rootTargetDto, childTargetDto));

    }


    private Target createTestTarget(Target parent){
        Target target = new Target();
        target.setRealm(realm);
        target.setParent(parent);
        targetsDAO.saveOrUpdate(target);
        return target;
    }

    private Mean createMean(List<Target> targets, Mean parent){
        Mean mean = new Mean();
        mean.setParent(parent);
        mean.setTargets(targets);
        meansDAO.saveOrUpdate(mean);
        return mean;
    }

}
