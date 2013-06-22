package com.kb.ukhocrawler.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class SearchDriver implements Runnable {

    private List<ChartDto> results = new ArrayList<ChartDto>();
    private Map<String, String> cookies;
    private String chartPrefix;
    public String getChartPrefix() {
        return chartPrefix;
    }

    private String chartNumber;
    private String chartSuffix;
    public String getChartSuffix() {
        return chartSuffix;
    }

    private int searchMethod;

    public SearchDriver(Map<String, String> cookies, String chartPrefix, String chartNumber, String chartSuffix, int searchMethod) {
        this.cookies = cookies;
        this.chartPrefix = chartPrefix;
        this.chartNumber = chartNumber;
        this.chartSuffix = chartSuffix;
        this.searchMethod = searchMethod;
    }

    protected void download() throws IOException {
        Util.print("Fetching %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartPrefix, Constant.CHART_NUMBER, chartNumber, Constant.CHART_SUFFIX, chartSuffix);

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
                chart.setPrefix(chartPrefix);
                chart.setChartNumber(chartNumber);
                chart.setSuffix(chartSuffix);
                String str = "";
                for (int i = 0; i < tds.size(); ++i) {
                    Element td = tds.get(i);
                    switch (i) {
                        case 0:
                            str = td.text();
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
                if (searchMethod == 1) {
                    if (str.contains(chartPrefix + chartNumber + chartSuffix)) {
                        results.add(chart);
                        break;
                    }
                } else {
                    results.add(chart);
                }
            }
        }

        Util.print("Done %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartPrefix, Constant.CHART_NUMBER, chartNumber, Constant.CHART_SUFFIX, chartSuffix);
    }

    public void run() {
        try {
            download();
        } catch (IOException e) {
            Util.error("Error: %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartPrefix, Constant.CHART_NUMBER, chartNumber, Constant.CHART_SUFFIX, chartSuffix);
            e.printStackTrace();
        }
    }

    public List<ChartDto> getResults() {
        return results;
    }

    public String getChartNumber() {
        return chartNumber;
    }
}
