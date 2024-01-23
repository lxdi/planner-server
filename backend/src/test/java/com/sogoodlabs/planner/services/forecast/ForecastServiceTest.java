package com.sogoodlabs.planner.services.forecast;

import com.sogoodlabs.planner.SpringTestConfig;
import com.sogoodlabs.planner.TestCreators;
import com.sogoodlabs.planner.configuration.main.DefaultRepPlanCreationJob;
import com.sogoodlabs.planner.model.dao.IRepPlanDAO;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.services.WeeksGenerator;
import com.sogoodlabs.planner.services.forecast.ForecastService;
import com.sogoodlabs.planner.util.DateUtils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class ForecastServiceTest extends SpringTestConfig {

    @Autowired
    private ForecastService forecastService;

    @Autowired
    private TestCreators testCreators;

    @Autowired
    private WeeksGenerator weeksGenerator;

    @Autowired
    private IRepPlanDAO repPlanDAO;


    @BeforeEach
    public void init() {
        weeksGenerator.generateYear(2023);
        //weeksGenerator.generateYear(2024);
    }

    @Test
    public void forecastTest_noReps() {
        initData(null);

        var report = forecastService.forecast(DateUtils.toDate("2023-03-25"), false);

        assertEquals("2023-04-23", report.getAllReport().getFinishAllTasksDate().toString());
        assertEquals(3, report.getLayerReports().size());
        assertNull(report.getAllReport().getMostRepsDoneDate());

    }

    @Test
    public void forecastTest_withReps() {

        var repPlan = repPlanDAO.findByTitle(DefaultRepPlanCreationJob.HALF_YEAR_LONG_REP_PLAN_TITLE_INTENSIVE);

        initData(repPlan);

        var report = forecastService.forecast(DateUtils.toDate("2023-03-25"), false);

        assertEquals("2023-05-28", report.getAllReport().getFinishAllTasksDate().toString());
        assertNotNull(report.getAllReport().getMostRepsDoneDate());

//        for (var layerReport : report.getLayerReports()) {
//            assertNotNull(layerReport.getMostRepsDoneDate());
//        }

    }

    private void initData(RepetitionPlan repPlan) {

        Realm realm1 = testCreators.createRealm();
        Mean mean1 = testCreators.createMean("test1", realm1);
        Layer layer1 = testCreators.createLayer(1, mean1, 1);

        for (int i = 0; i< 5; i++) {
            testCreators.createTask("test task", layer1, repPlan);
        }

        Layer layer12 = testCreators.createLayer(1, mean1, 2);

        for (int i = 0; i< 3; i++) {
            testCreators.createTask("test task", layer12, null);
        }

        Realm realm2 = testCreators.createRealm();
        Mean mean2 = testCreators.createMean("test1", realm2);
        Layer layer21 = testCreators.createLayer(1, mean2, 1);

        for (int i = 0; i< 4; i++) {
            testCreators.createTask("test task", layer21, repPlan);
        }

        Slot slot1 = testCreators.createSlot(realm1, 2, DaysOfWeek.mon);
        Slot slot12 = testCreators.createSlot(realm1, 2, DaysOfWeek.wed);
        Slot slot2 = testCreators.createSlot(realm2, 2, DaysOfWeek.tue);
    }

}
