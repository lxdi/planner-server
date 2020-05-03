package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.IMeansDAO;
import com.sogoodlabs.planner.model.entities.Mean;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 08.04.2018.
 */

public abstract class AbstractTestsWithTargetsWithMeans extends AbstractTestsWithTargets {

    @Autowired
    protected IMeansDAO meansDao;

    protected String parentMeanTitle = "Parent mean";
    protected String childMeanTitle = "Child mean";
    protected String child2MeanTitle = "Child mean2";

    @Override
    @Before
    public void init(){
        super.init();

        Mean parentMean = new Mean(parentMeanTitle, realm);
        parentMean.getTargets().add(targetsDAO.getTargetByTitle(defaultParentTarget));
        meansDao.saveOrUpdate(parentMean);

        Mean childMean = new Mean(childMeanTitle, realm);
        childMean.setParent(parentMean);
        childMean.getTargets().add(targetsDAO.getTargetByTitle(defaultChildTarget));
        childMean.getTargets().add(targetsDAO.getTargetByTitle(defaultChild2Target));
        meansDao.saveOrUpdate(childMean);

        Mean childMean2 = new Mean(child2MeanTitle, realm);
        childMean2.setParent(parentMean);
        childMean2.getTargets().add(targetsDAO.getTargetByTitle(defaultChildChildTarget));
        meansDao.saveOrUpdate(childMean2);

        assertTrue(meansDao.meanById(1).getTitle().equals(parentMeanTitle));
        assertTrue(meansDao.meanById(1).getRealm().getTitle().equals(realm.getTitle()));
        assertTrue(meansDao.meanById(1).getTargets().get(0).getTitle().equals(defaultParentTarget));

        assertTrue(meansDao.meanById(2).getTitle().equals(childMeanTitle));
        assertTrue(meansDao.meanById(2).getRealm().getTitle().equals(realm.getTitle()));
        assertTrue(meansDao.meanById(2).getTargets().size()==2);

        assertTrue(meansDao.meanById(3).getTitle().equals(child2MeanTitle));
        assertTrue(meansDao.meanById(3).getRealm().getTitle().equals(realm.getTitle()));
        assertTrue(meansDao.meanById(3).getTargets().get(0).getTitle().equals(defaultChildChildTarget));
    }

}
