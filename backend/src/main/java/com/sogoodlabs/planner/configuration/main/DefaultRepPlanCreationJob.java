package com.sogoodlabs.planner.configuration.main;

import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultRepPlanCreationJob implements ApplicationListener<ContextRefreshedEvent> {

    public final static String ONE_YEAR_LONG_REP_PLAN_TITLE = "One Year Long (2-6-12-24-48)";
    public final static String HALF_YEAR_LONG_REP_PLAN_TITLE = "Half Year Long (2-6-10-16-24)";
    public final static String HALF_YEAR_LONG_REP_PLAN_TITLE_INTENSIVE = "Intensive Half Year Long (1-2-4-6-10-16-24)";
    public final static String ONE_AND_HALF_MONTH_DAY_STEP_TITLE = "[Day] Memorizing (1-2-4-7-11-19-34)";

    @Autowired
    IRepPlanDAO repPlanDAO;

    //@Transactional(propagation= Propagation.REQUIRES_NEW)
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        getOrCreateRepPlan(ONE_YEAR_LONG_REP_PLAN_TITLE, new int[]{2, 6, 12, 24, 48}, false);
        getOrCreateRepPlan(HALF_YEAR_LONG_REP_PLAN_TITLE, new int[]{2, 6, 10, 16, 24}, false);
        getOrCreateRepPlan(HALF_YEAR_LONG_REP_PLAN_TITLE_INTENSIVE, new int[]{1, 2, 4, 6, 10, 16, 24}, false);
        getOrCreateRepPlan(ONE_AND_HALF_MONTH_DAY_STEP_TITLE, new int[]{1, 2, 4, 7, 11, 19, 34}, true);
    }

    private void getOrCreateRepPlan(String title, int[] plan, boolean dayStep){
        RepetitionPlan repetitionPlan = repPlanDAO.findByTitle(title);
        if(repetitionPlan==null){
            RepetitionPlan defaultRepPlan = new RepetitionPlan();
            defaultRepPlan.setId(UUID.randomUUID().toString());
            defaultRepPlan.setTitle(title);
            defaultRepPlan.setPlan(plan);
            defaultRepPlan.setDayStep(dayStep);
            repPlanDAO.save(defaultRepPlan);
        }
    }


}
