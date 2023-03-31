package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dto.ForecastLayerReport;
import com.sogoodlabs.planner.model.dto.ForecastReport;
import com.sogoodlabs.planner.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Set;

@Service
public class ForecastReportService {

    @Autowired
    private IDayDao dayDao;

    public void enrichReport(ForecastReport report, Week week, Task task, Set<Repetition> reps) {

        var layer = task.getLayer();
        var layerReport = getLayerReport(report, layer);

        var wednsday = dayDao.findByWeek(week).stream()
                .filter(day -> day.getWeekDay() == DaysOfWeek.wed)
                .findFirst().get();

        if(layerReport.getFinishAllTasksDate() == null || layerReport.getFinishAllTasksDate().before(wednsday.getDate())) {
            layerReport.setFinishAllTasksDate(wednsday.getDate());

            var dayOf70reps = calculate70PercentReps(reps);

            if(dayOf70reps != null) {
                layerReport.setFinish70percentReps(dayOf70reps.getDate());
            }
        }
    }

    public void finishReport(ForecastReport report, Date date, Set<Repetition> reps) {
        var allReport = new ForecastLayerReport();
        allReport.setFinishAllTasksDate(date);
        report.setAllReport(allReport);
        // TODO
    }

    private ForecastLayerReport getLayerReport(ForecastReport report, Layer layer) {
        var layerReports = report.getLayerReports().stream().filter(r -> r.getLayer().getId().equals(layer.getId())).toList();

        if(layerReports.size() > 1) {
            throw new RuntimeException("Too many layer reports found");
        }

        if (layerReports.size() == 0){
            var layerReport = new ForecastLayerReport();
            layerReport.setLayer(layer);
            report.getLayerReports().add(layerReport);
            return layerReport;
        }

        return layerReports.get(0);
    }

    private Day calculate70PercentReps(Set<Repetition> reps) {
        //TODO
        return null;
    }

}
