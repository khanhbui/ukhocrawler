package com.kb.ukhocrawler.service;

import com.kb.ukhocrawler.dto.OutputDto;

public abstract class InfoService extends RunnableService {
    protected OutputDto info;

    public OutputDto getInfo() {
        return this.info;
    }

    public InfoService(OutputDto info) {
        super();
        this.info = info;
    }
}