package com.kb.ukhocrawler.dto.pub;

import com.kb.ukhocrawler.dto.OutputDto;

public class PubDto extends OutputDto {
    private String number;
    private String title;
    private String subTitle;
    private String type;
    private String subType;
    private String editionNo;
    private String pubYear;

    private String pubId;

    public String toString() {
        return String.format("{%s, %s, %s, %s, %s, %s, %s, %s}",  this.number, this.title, this.subTitle, this.type, this.subType, this.editionNo, this.pubYear, this.pubId);
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getEditionNo() {
        return editionNo;
    }

    public void setEditionNo(String editionNo) {
        this.editionNo = editionNo;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }
}
