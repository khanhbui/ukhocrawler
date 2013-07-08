package com.kb.ukhocrawler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kb.ukhocrawler.dto.InputDto;
import com.kb.ukhocrawler.dto.OutputDto;

public abstract class SearchService extends RunnableService {
    protected Map<String, String> cookies;
    protected int searchMethod;

    protected InputDto input;
    protected List<OutputDto> results;

    public abstract InputDto getInput();
    public List<OutputDto> getResults() {
        return this.results;
    }

    public SearchService(Map<String, String> cookies, InputDto input, int searchMethod) {
        this.cookies = cookies;
        this.input = input;
        this.searchMethod = searchMethod;
        this.results = new ArrayList<OutputDto>();
    }
}
