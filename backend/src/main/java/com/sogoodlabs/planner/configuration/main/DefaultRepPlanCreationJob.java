package com.sogoodlabs.planner.configuration.main;

import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultRepPlanCreationJob implements ApplicationListener<ContextRefreshedEvent> {

    public final static String ONE_YEAR_LONG_REP_PLAN_TITLE = "One Year Long (2-6-12-24-48)";
    public final static String HALF_YEAR_LONG_REP_PLAN_TITLE = "Half Year Long (2-6-10-16-24)";
    public final static String HALF_YEAR_LONG_REP_PLAN_TITLE_INTENSIVE = "Intensive Half Year Long (1-2-4-6-10-16-24)";

    @Autowired
    IRepPlanDAO repPlanDAO;

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        getOrCreateRepPlan(ONE_YEAR_LONG_REP_PLAN_TITLE, new int[]{2, 6, 12, 24, 48});
        getOrCreateRepPlan(HALF_YEAR_LONG_REP_PLAN_TITLE, new int[]{2, 6, 10, 16, 24});
        getOrCreateRepPlan(HALF_YEAR_LONG_REP_PLAN_TITLE_INTENSIVE, new int[]{1, 2, 4, 6, 10, 16, 24});
    }

    private void getOrCreateRepPlan(String title, int[] plan){
        RepetitionPlan repetitionPlan = repPlanDAO.getByTitle(title);
        if(repetitionPlan==null){
            RepetitionPlan defaultRepPlan = new RepetitionPlan();
            defaultRepPlan.setTitle(title);
            defaultRepPlan.setPlan(plan);
            repPlanDAO.save(defaultRepPlan);
        }
    }


}
