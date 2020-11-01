package com.sogoodlabs.planner.model.dao;

import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.RepetitionPlan;
import com.sogoodlabs.planner.model.entities.SpacedRepetitions;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.sogoodlabs.planner.util.DateUtils;
import com.sogoodlabs.planner.test_configs.SpringTestConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@Transactional
public class RepDaoTests extends SpringTestConfig {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    IRepDAO repDAO;

    @Autowired
    ISpacedRepDAO spacedRepDAO;

    @Autowired
    IRepPlanDAO repPlanDAO;

    Repetition repetition;
    Repetition repetition2;
    Repetition repetition3;
    Repetition repetition4;

    @Override
    public void init(){
        super.init();

        RepetitionPlan repetitionPlanDay = new RepetitionPlan();
        repetitionPlanDay.setDayStep(true);
        repPlanDAO.save(repetitionPlanDay);

        RepetitionPlan repetitionPlanMonth = new RepetitionPlan();
        repetitionPlanMonth.setDayStep(false);
        repPlanDAO.save(repetitionPlanMonth);

        SpacedRepetitions spacedRepetitionsMonth = new SpacedRepetitions();
        spacedRepetitionsMonth.setRepetitionPlan(repetitionPlanMonth);
        spacedRepDAO.save(spacedRepetitionsMonth);

        SpacedRepetitions spacedRepetitionsDay = new SpacedRepetitions();
        spacedRepetitionsDay.setRepetitionPlan(repetitionPlanDay);
        spacedRepDAO.save(spacedRepetitionsDay);

        repetition = createRep(spacedRepetitionsMonth, "2019-03-14", null);
        repetition2 = createRep(spacedRepetitionsMonth, "2019-01-10", null);
        repetition3 = createRep(spacedRepetitionsMonth, "2019-01-10", "2019-02-10");

        repetition4 = createRep(spacedRepetitionsDay, "2020-03-10", null);

        entityManager.unwrap(Session.class).flush();
        entityManager.unwrap(Session.class).clear();

    }


    @Test
    public void getFinishedWithPlanDateInRangeTest(){

        check("2019-03-10", "2019-03-17", foundReps -> {
            assertEquals(1, foundReps.size());
            assertEquals(foundReps.get(0).getId(), repetition.getId());
        });

        check("2019-01-09", "2019-02-12", foundReps -> {
            assertEquals(1, foundReps.size());
            assertEquals(foundReps.get(0).getId(), repetition2.getId());
        });

        check("2019-01-10", "2019-02-12", foundReps -> {
            assertEquals(1, foundReps.size());
            assertEquals(foundReps.get(0).getId(), repetition2.getId());
        });

        check("2019-01-05", "2019-01-10", foundReps -> {
            assertEquals(1, foundReps.size());
            assertEquals(foundReps.get(0).getId(), repetition2.getId());
        });

        check("2019-01-05", "2019-04-10", foundReps -> {
            assertEquals(2, foundReps.size());
            assertEquals(foundReps.get(0).getId(), repetition2.getId());
            assertEquals(foundReps.get(1).getId(), repetition.getId());
        });

    }

    @Test
    public void numberOfRepetitionsInRangeTest(){
        assertEquals(3,
                repDAO.numberOfRepetitionsInRange(
                        DateUtils.toDate("2019-01-08"), DateUtils.toDate("2020-04-09"), false));
    }

    @Test
    public void numberOfRepetitionsInRangeMonthStepTest(){
        assertEquals(2,
                repDAO.numberOfRepetitionsInRangeMonthStep(
                        DateUtils.toDate("2019-01-08"), DateUtils.toDate("2020-04-09"), false));
    }

    private void check(String date1, String date2, Consumer<List<Repetition>> consumer){
        consumer.accept(repDAO.getUnFinishedWithPlanDateInRange(
                DateUtils.toDate(date1),
                DateUtils.toDate(date2)));
    }

    private Repetition createRep(SpacedRepetitions spacedRepetitions, String planDate, String factDate){
        Repetition rep = new Repetition();
        rep.setSpacedRepetitions(spacedRepetitions);
        rep.setPlanDate(DateUtils.toDate(planDate));
        if(factDate!=null) {
            rep.setFactDate(DateUtils.toDate(factDate));
        }
        repDAO.save(rep);
        return rep;
    }

}
