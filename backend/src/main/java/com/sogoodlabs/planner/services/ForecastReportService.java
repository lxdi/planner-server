package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dto.ForecastLayerReport;
import com.sogoodlabs.planner.model.dto.ForecastReport;
import com.sogoodlabs.planner.model.entities.Repetition;
import com.sogoodlabs.planner.model.entities.Task;
import com.sogoodlabs.planner.model.entities.Week;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Set;

@Service
public class ForecastReportService {

    public void enrichReport(ForecastReport report, Week week, Task task, Set<Repetition> reps) {
        // TODO
    }

    public void finishReport(ForecastReport report, Date date, Set<Repetition> reps) {
        var allReport = new ForecastLayerReport();
        allReport.setFinishAllTasksDate(date);
        report.setAllReport(allReport);
        // TODO
    }

}
