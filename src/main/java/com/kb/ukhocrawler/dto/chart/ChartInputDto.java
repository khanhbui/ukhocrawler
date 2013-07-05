package com.kb.ukhocrawler.dto.chart;

import com.kb.ukhocrawler.dto.InputDto;

public class ChartInputDto extends InputDto {
    private String chartPrefix;
    private String chartNumber;
    private String chartSuffix;

    public ChartInputDto(String chartPrefix, String chartNumber, String chartSuffix) {
        this.chartPrefix = chartPrefix;
        this.chartNumber = chartNumber;
        this.chartSuffix = chartSuffix;
    }

    public String getChartPrefix() {
        return chartPrefix;
    }

    public String getChartNumber() {
        return chartNumber;
    }

    public String getChartSuffix() {
        return chartSuffix;
    }
}
