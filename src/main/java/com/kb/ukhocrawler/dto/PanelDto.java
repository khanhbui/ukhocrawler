package com.kb.ukhocrawler.dto;

public class PanelDto {
    private String panelName;
    private String areaName;
    private String naturalScale;
    private String northLimit;
    private String southLimit;
    private String eastLimit;
    private String westLimit;

    public String getPanelName() {
        return panelName;
    }
    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }
    public String getAreaName() {
        return areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getNaturalScale() {
        return naturalScale;
    }
    public void setNaturalScale(String naturalScale) {
        this.naturalScale = naturalScale;
    }
    public String getNorthLimit() {
        return northLimit;
    }
    public void setNorthLimit(String northLimit) {
        this.northLimit = northLimit;
    }
    public String getSouthLimit() {
        return southLimit;
    }
    public void setSouthLimit(String southLimit) {
        this.southLimit = southLimit;
    }
    public String getEastLimit() {
        return eastLimit;
    }
    public void setEastLimit(String eastLimit) {
        this.eastLimit = eastLimit;
    }
    public String getWestLimit() {
        return westLimit;
    }
    public void setWestLimit(String westLimit) {
        this.westLimit = westLimit;
    }
}
