package com.kb.ukhocrawler.dto.pub;

import com.kb.ukhocrawler.dto.InputDto;

public class PubInputDto extends InputDto {
    private String pubNumber;

    public PubInputDto(String pubNumber) {
        this.pubNumber = pubNumber;
    }

    public String getPubNumber() {
        return pubNumber;
    }
}
