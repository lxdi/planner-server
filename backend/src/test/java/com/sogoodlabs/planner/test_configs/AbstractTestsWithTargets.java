package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.*;
import com.sogoodlabs.planner.model.entities.Realm;
import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
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

    protected Target parentTarget;
    protected Target target;
    protected Target target2;
    protected Target target3;

    protected String defaultParentTarget = "drefault parent";
    protected String defaultChildTarget = "default child";
    protected String defaultChild2Target = "default child2";
    protected String defaultChildChildTarget = "default child child";

    @Before
    public void init(){
        super.init();
        realm = realmDAO.createRealm("test realm");

        parentTarget = new Target(defaultParentTarget, realm);
        targetsDAO.saveOrUpdate(parentTarget);

        target2 = new Target(defaultChild2Target, realm);
        target2.setParent(parentTarget);
        targetsDAO.saveOrUpdate(target2);

        target = new Target(defaultChildTarget, realm);
        target.setParent(parentTarget);
        target.setNext(target2);
        targetsDAO.saveOrUpdate(target);

        target3 = new Target(defaultChildChildTarget, realm);
        target3.setParent(target2);
        targetsDAO.saveOrUpdate(target3);

        assertTrue(parentTarget.getId()>0);
        assertTrue(target.getId()>0);
        assertTrue(target2.getId()>0);
        assertTrue(target3.getId()>0);
    }

}
