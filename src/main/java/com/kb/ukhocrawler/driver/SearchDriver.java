package com.kb.ukhocrawler.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class SearchDriver {

    List<ChartDto> charts = new ArrayList<ChartDto>();

    public List<ChartDto> getCharts() {
        return charts;
    }

    public void submit(Map<String, String> cookies, String chartPrefix, String chartNumber, String chartSuffix) throws IOException {
        Util.print("Fetching %s...", Constant.SEARCH_URL);

        Document doc = Util.getConnection(Constant.SEARCH_URL)
                .cookies(cookies)
                .data(Constant.CHART_PREFIX, chartPrefix)
                .data(Constant.CHART_NUMBER, chartNumber)
                .data(Constant.CHART_SUFFIX, chartSuffix)
                .post();

        Elements tbodies = doc.getElementsByTag("tbody");
        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");

                ChartDto chart = new ChartDto();
                for (int i = 0; i < tds.size(); ++i) {
                    Element td = tds.get(i);
                    switch (i) {
                        case 0:
                            chart.setChartNumber(td.text());
                            break;
                        case 1:
                            chart.setChartType(td.text());
                            break;
                        case 3:
                            chart.setChartTitle(td.text());
                        case 4:
                            Element a = td.getElementsByTag("a").first();
                            chart.setChartInfoId(a == null ? "" : Util.extract(a.attr("href"), "(.*)ChartID=(\\d+)"));
                            break;
                        case 5:
                            a = td.getElementsByTag("a").first();
                            chart.setChartPreviewId(a == null ? "" : Util.extract(a.attr("href"), "(.*)ChartID=(\\d+)(.*)"));
                            break;
                        default:
                            break;
                    }
                }
                charts.add(chart);
            }
        }

        Util.print("Done %s.", Constant.SEARCH_URL);
    }

    public void save() throws IOException {
        String path = String.format(Constant.GENERAL_INFO_PATH, File.separator, File.separator);
        PrintStream out = null;
        try {
            Util.createDirs(path);
            out = new PrintStream(new FileOutputStream(path));
            out.print(charts.toString());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
