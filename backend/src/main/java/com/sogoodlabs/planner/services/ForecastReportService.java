package com.sogoodlabs.planner.services;

import com.sogoodlabs.planner.model.dao.IDayDao;
import com.sogoodlabs.planner.model.dto.ForecastLayerReport;
import com.sogoodlabs.planner.model.dto.ForecastReport;
import com.sogoodlabs.planner.model.entities.*;
import com.sogoodlabs.planner.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Set;

@Service
public class ForecastReportService {

    Logger log = LoggerFactory.getLogger(ForecastReportService.class);

    @Value("${forecast.report.most.reps.done.percentage:0.70}")
    private float mostRepsDonePercents;

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

            var mostRepsDoneDate = calculateMostRepsDoneDate(reps);

            if(mostRepsDoneDate != null) {
                layerReport.setMostRepsDoneDate(mostRepsDoneDate.getDate());
            }
        }
    }

    public void finishReport(ForecastReport report, Date date, Set<Repetition> reps) {
        var allReport = new ForecastLayerReport();
        allReport.setId("all");
        allReport.setFinishAllTasksDate(date);
        var mostRepsDoneDay = calculateMostRepsDoneDate(reps);

        if(mostRepsDoneDay != null) {
            allReport.setMostRepsDoneDate(calculateMostRepsDoneDate(reps).getDate());
        }

        report.setAllReport(allReport);
    }

    private ForecastLayerReport getLayerReport(ForecastReport report, Layer layer) {
        var layerReports = report.getLayerReports().stream().filter(r -> r.getLayer().getId().equals(layer.getId())).toList();

        if(layerReports.size() > 1) {
            throw new RuntimeException("Too many layer reports found for layer " + layer.getId());
        }

        if (layerReports.size() == 0){
            var layerReport = new ForecastLayerReport();
            layerReport.setLayer(layer);
            layerReport.setId(layer.getId());
            report.getLayerReports().add(layerReport);
            return layerReport;
        }

        return layerReports.get(0);
    }

    private Day calculateMostRepsDoneDate(Set<Repetition> reps) {
        if (reps == null || reps.isEmpty()) {
            return null;
        }

        if (reps.size() == 1) {
            return reps.stream().findFirst().get().getPlanDay();
        }

        Date minDate = null;
        Date maxDate = null;

        for(var rep : reps) {
            var planDate = rep.getPlanDay().getDate();

            if(minDate == null || minDate.after(planDate)){
                minDate = planDate;
            }

            if(maxDate == null || maxDate.before(planDate)) {
                maxDate = planDate;
            }

        }

        var daysSpan = DateUtils.differenceInDays(minDate, maxDate);
        var targetDate = DateUtils.addDays(minDate, Math.round(daysSpan * mostRepsDonePercents));

        Repetition mostAccurateRep = null;
        int diffDaysMin = Integer.MAX_VALUE;

        for(var rep : reps) {
            var planDate = rep.getPlanDay().getDate();
            var diffDays = DateUtils.differenceInDays(targetDate, planDate);

            if(diffDays < diffDaysMin) {
                diffDaysMin = diffDays;
                mostAccurateRep = rep;
            }
        }

        if (mostAccurateRep == null) {
            log.warn("Couldn't find mostRepsDone rep");
            return null;
        }

        return mostAccurateRep.getPlanDay();
    }

}
