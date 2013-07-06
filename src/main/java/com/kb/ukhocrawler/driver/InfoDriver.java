package com.kb.ukhocrawler.driver;

import com.kb.ukhocrawler.dto.OutputDto;

public abstract class InfoDriver extends RunnableDriver {
    protected OutputDto info;

    public InfoDriver(OutputDto info) {
        super();
        this.info = info;
    }
}