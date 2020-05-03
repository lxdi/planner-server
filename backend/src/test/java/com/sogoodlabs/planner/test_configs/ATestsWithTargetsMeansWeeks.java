package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.dao.IWeekDAO;
import com.sogoodlabs.planner.services.WeeksGenerator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Alexander on 22.04.2018.
 */
public abstract class ATestsWithTargetsMeansWeeks extends AbstractTestsWithTargetsWithMeans {

    @Autowired
    protected IWeekDAO weekDAO;

    @Autowired
    protected WeeksGenerator weeksGenerator;


    @Before
    @Override
    public void init(){
        super.init();
        weeksGenerator.generate(new ArrayList<>(Arrays.asList(2017, 2018, 2019)));
    }

}
