package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.IHQuarterDAO;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.services.QuarterGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;


public abstract class ATestsWithTargetsMeansQuartalsGenerated extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    QuarterGenerator quarterGenerator;

    @Autowired
    protected IHQuarterDAO hquarterDAO;

    @Before
    @Override
    public void init(){
        super.init();
        quarterGenerator.generate(new ArrayList<>(Arrays.asList(2018, 2019)));
    }

}
