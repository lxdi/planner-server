package configuration.main;

import model.dao.IRepPlanDAO;
import model.entities.RepetitionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultRepPlanCreationJob implements ApplicationListener<ContextRefreshedEvent> {

    public static String DEFAULT_REP_PLAN_TITLE = "6-12-24-48";

    @Autowired
    IRepPlanDAO repPlanDAO;

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        List<RepetitionPlan> repPlans = repPlanDAO.getAll();
        if(repPlans.size()==0){
            RepetitionPlan defaultRepPlan = new RepetitionPlan();
            defaultRepPlan.setTitle(DEFAULT_REP_PLAN_TITLE);
            defaultRepPlan.setPlan(new int[]{6, 12, 24, 48});
            repPlanDAO.save(defaultRepPlan);
        }
    }

}
