package com.kb.ukhocrawler;

public class ChartDto {
    private String chartNumber;
    private String chartType;
    private String chartTitle;
    private String chartInfoId;
    private String chartPreviewId;
    
    public ChartDto(){
        
    }
    
    public void setChartNumber(String chartNumber) {
        this.chartNumber = chartNumber;
    }
    
    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
    
    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }
    
    public void setChartInfoId(String chartId) {
        this.chartInfoId = chartId;
    }
    
    public void setChartPreviewId(String chartId) {
        this.chartPreviewId = chartId;
    }
    
    public String getChartNumber() {
        return this.chartNumber;
    }
    
    public String getChartType() {
        return this.chartType;
    }
    
    public String getChartTitle() {
        return this.chartTitle;
    }
    
    public String getInfoChartId() {
        return this.chartInfoId;
    }
    
    public String getPreviewChartId() {
        return this.chartPreviewId;
    }

    public String toString() {
        return String.format("{%s, %s, %s, %s, %s}\n", chartNumber, chartType, chartTitle, chartInfoId, chartPreviewId);
    }
}
