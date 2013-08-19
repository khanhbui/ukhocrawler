package com.kb.ukhocrawler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    protected String nextPage(Document doc) {
        //<a href="browse_PUBS_results.asp?FilterMethod=1&amp;PubNumber=1&amp;offset=10" title="Next 10" in_tag="ul" kaspersky_status="skipped">Next 10</a>
        Element a = doc.select("a[title=Next 10]").first();
        return a == null ? null : a.attr("href");
    }
}
