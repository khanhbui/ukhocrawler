package com.kb.ukhocrawler.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kb.ukhocrawler.dto.InputDto;
import com.kb.ukhocrawler.dto.chart.ChartDto;

public abstract class SearchDriver extends RunnableDriver {
    protected Map<String, String> cookies;
    protected int searchMethod;

    protected InputDto input;
    protected List<ChartDto> results = new ArrayList<ChartDto>();

    public InputDto getInput() {
        return input;
    }

    public List<ChartDto> getResults() {
        return results;
    }

    public SearchDriver(Map<String, String> cookies, InputDto input, int searchMethod) {
        this.cookies = cookies;
        this.input = input;
        this.searchMethod = searchMethod;
    }
}
