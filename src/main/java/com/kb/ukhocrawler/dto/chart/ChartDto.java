package com.kb.ukhocrawler.dto.chart;

import java.util.List;

import com.kb.ukhocrawler.dto.OutputDto;

public class ChartDto extends OutputDto {
    private String prefix;
    private String chartNumber;
    private String suffix;
    private String chartTitle;
    private String publicationDate;
    private String latestEditionDate;
    private String chartSize;
    private String image;
    private List<PanelDto> panels;

    private String chartType;
    private String chartInfoId;
    private String chartPreviewId;

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
        return String.format("{%s, %s, %s, %s, %s, %s, %s}", prefix, chartNumber, suffix, chartType, chartTitle, chartInfoId, chartPreviewId);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLatestEditionDate() {
        return latestEditionDate;
    }

    public void setLatestEditionDate(String latestEditionDate) {
        this.latestEditionDate = latestEditionDate;
    }

    public String getChartSize() {
        return chartSize;
    }

    public void setChartSize(String chartSize) {
        this.chartSize = chartSize;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<PanelDto> getPanels() {
        return panels;
    }

    public void setPanels(List<PanelDto> panels) {
        this.panels = panels;
    }
}
