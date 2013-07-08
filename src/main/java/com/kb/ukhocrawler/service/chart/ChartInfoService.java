package com.kb.ukhocrawler.service.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.chart.PanelDto;
import com.kb.ukhocrawler.service.InfoService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class ChartInfoService extends InfoService {

    public ChartInfoService(ChartDto info) {
        super(info);
    }

    @Override
    public ChartDto getInfo() {
        return (ChartDto)this.info;
    }

    @Override
    protected void download() throws IOException {
        ChartDto chart = this.getInfo();
        if (chart.getInfoChartId().equals("")) {
            return;
        }

        String url = String.format(Constant.INFO_URL, chart.getInfoChartId());
        Util.print("Fetching info from %s...", url);

        Document doc = Util.getConnection(url).get();

        if (doc != null) {
            Elements uls = doc.select("ul[class=chart-details]");
            if (uls != null) {
                List<PanelDto> panels = new ArrayList<PanelDto>();
                for (int i = 0; i < uls.size(); ++i) {
                    Elements lis = uls.get(i).select("li");
                    switch (i) {
                        case 0:
                            for (int j = 0; j < lis.size(); ++j) {
                                String text = lis.get(j).text();
                                String str = Util.extract(text, "(.*)Chart Title: (.*)", 2);
                                if (!str.equals("")) {
                                    chart.setChartTitle(str);
                                } else {
                                    str = Util.extract(text, "(.*)Publication Date: (.*)", 2);
                                    if (!str.equals("")) {
                                        chart.setPublicationDate(str);
                                    } else {
                                        str = Util.extract(text, "(.*)Latest Edition date: (.*)", 2);
                                        if (!str.equals("")) {
                                            chart.setLatestEditionDate(str);
                                        } else {
                                            str = Util.extract(text, "(.*)Chart Size: (.*)", 2);
                                            if (!str.equals("")) {
                                                chart.setChartSize(str);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            PanelDto panel = new PanelDto();
                            for (int j = 0; j < lis.size(); ++j) {
                                String text = lis.get(j).text();
                                String str = Util.extract(text, "(.*)Panel Name (.*)", 2);
                                if (!str.equals("")) {
                                    panel.setPanelName(str);
                                } else {
                                    str = Util.extract(text, "(.*)Area Name (.*)", 2);
                                    if (!str.equals("")) {
                                        panel.setAreaName(str);
                                    } else {
                                        str = Util.extract(text, "(.*)Natural Scale (.*)", 2);
                                        if (!str.equals("")) {
                                            panel.setNaturalScale(str);
                                        } else {
                                            str = Util.extract(text, "(.*)North Limit (.*)", 2);
                                            if (!str.equals("")) {
                                                panel.setNorthLimit(str);
                                            } else {
                                                str = Util.extract(text, "(.*)East Limit (.*)", 2);
                                                if (!str.equals("")) {
                                                    panel.setEastLimit(str);
                                                } else {
                                                    str = Util.extract(text, "(.*)South Limit (.*)", 2);
                                                    if (!str.equals("")) {
                                                        panel.setSouthLimit(str);
                                                    } else {
                                                        str = Util.extract(text, "(.*)West Limit (.*)", 2);
                                                        if (!str.equals("")) {
                                                            panel.setWestLimit(str);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            panels.add(panel);
                            break;
                    }
                }
                chart.setPanels(panels);
            }
        }
        Util.print("Done %s.", url);
    }

    @Override
    protected void onError() {
        ChartDto chart = this.getInfo();
        Util.error("ChartInfoService::onError: %s %s", String.format(Constant.INFO_URL, chart.getInfoChartId()), this.info);
    }
}