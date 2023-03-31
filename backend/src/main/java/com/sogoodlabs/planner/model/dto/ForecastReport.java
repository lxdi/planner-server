package com.sogoodlabs.planner.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ForecastReport {

    private List<ForecastLayerReport> layerReports = new ArrayList<>();
    private ForecastLayerReport allReport;

    public List<ForecastLayerReport> getLayerReports() {
        return layerReports;
    }

    public void setLayerReports(List<ForecastLayerReport> layerReports) {
        this.layerReports = layerReports;
    }

    public ForecastLayerReport getAllReport() {
        return allReport;
    }

    public void setAllReport(ForecastLayerReport allReport) {
        this.allReport = allReport;
    }
}
