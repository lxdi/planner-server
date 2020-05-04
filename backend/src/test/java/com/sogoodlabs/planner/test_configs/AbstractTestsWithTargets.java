package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 07.04.2018.
 */

public abstract class AbstractTestsWithTargets extends SpringTestConfig {

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
        super.init();
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
