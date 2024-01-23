package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.planner.services.forecast.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/forecast")
public class ForecastReportController {

    @Autowired
    private ForecastService forecastService;

    @Autowired
    private CommonMapper commonMapper;

    @GetMapping("/all")
    public List<Map<String, Object>> getReport() {
        var report = forecastService.forecast();
        var res = new ArrayList<Map<String, Object>>();

        if(report.getAllReport() != null) {
            res.add(commonMapper.mapToDto(report.getAllReport()));
        }

        if(report.getLayerReports().size() > 0) {
            res.addAll(report.getLayerReports().stream().map(commonMapper::mapToDto).toList());
        }

        return res;
    }
}
