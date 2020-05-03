package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import com.sogoodlabs.planner.model.entities.HQuarter;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;

@Deprecated
public abstract class ATestsWithTargetsMeansQuartals extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IHQuarterDAO hquartalDAO;

    protected HQuarter firstHQuarter;
    protected HQuarter secondHQuarter;
    protected HQuarter thirdHQuarter;

    @Before
    @Override
    public void init(){
        super.init();
        firstHQuarter = new HQuarter();
        hquartalDAO.saveOrUpdate(firstHQuarter);

        secondHQuarter = new HQuarter();
        hquartalDAO.saveOrUpdate(secondHQuarter);

        thirdHQuarter = new HQuarter();
        hquartalDAO.saveOrUpdate(thirdHQuarter);

        assertTrue(firstHQuarter.getId()>0);
        assertTrue(secondHQuarter.getId()>0);
        assertTrue(thirdHQuarter.getId()>0);

    }

}
