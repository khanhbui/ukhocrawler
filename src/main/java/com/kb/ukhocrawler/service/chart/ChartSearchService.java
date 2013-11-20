package com.kb.ukhocrawler.service.chart;

import java.io.IOException;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.chart.ChartInputDto;
import com.kb.ukhocrawler.service.SearchService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class ChartSearchService extends SearchService {

    public ChartSearchService(Map<String, String> cookies, ChartInputDto input, int searchMethod) {
        super(cookies, input, searchMethod);
    }

    @Override
    public ChartInputDto getInput() {
        return (ChartInputDto)input;
    }

    @Override
    protected void download() throws IOException {
        ChartInputDto chartInput = this.getInput();

        Util.print("Fetching %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());

        Document doc = Util.getConnection(Constant.SEARCH_URL)
                .cookies(cookies)
                .data(Constant.CHART_PREFIX, chartInput.getChartPrefix())
                .data(Constant.CHART_NUMBER, chartInput.getChartNumber())
                .data(Constant.CHART_SUFFIX, chartInput.getChartSuffix())
                .post();

        Elements tbodies = doc.getElementsByTag("tbody");
        Boolean found = this.extractTbodies(tbodies);

        String nextPage = null;
        while (!found && (nextPage = this.nextPage(doc)) != null) {
            doc = Util.getConnection(Constant.MAIN_URL + nextPage)
                    .cookies(cookies)
                    .get();
            tbodies = doc.getElementsByTag("tbody");
            found = this.extractTbodies(tbodies);
            Util.print("----- Done %s", Constant.MAIN_URL + nextPage);
        }
    }

    private Boolean extractTbodies(Elements tbodies) {
        ChartInputDto chartInput = this.getInput();

        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");

                ChartDto chart = new ChartDto();
                chart.setPrefix(chartInput.getChartPrefix());
                chart.setChartNumber(chartInput.getChartNumber());
                chart.setSuffix(chartInput.getChartSuffix());
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
                            chart.setChartInfoId(a == null ? "" : Util.extract(a.attr("href"), "(.*)ChartID=(\\d+)", 2));
                            break;
                        case 5:
                            a = td.getElementsByTag("a").first();
                            chart.setChartPreviewId(a == null ? "" : Util.extract(a.attr("href"), "(.*)ChartID=(\\d+)(.*)", 2));
                            break;
                        default:
                            break;
                    }
                }
                if (searchMethod == 1) {
                    String a = (chartInput.getChartPrefix().trim() + chartInput.getChartNumber().trim() + chartInput.getChartSuffix().trim()).toLowerCase();
                    String b = str.toLowerCase().trim();
                    Util.print("Comparing... |%s|==|%s|", a, b);
                    if (a.equals(b)) {
                    	Util.print("--Found... |%s|==|%s|=true", a, b);
                        results.add(chart);
                        return true;
                    }
                } else {
                    results.add(chart);
                }
            }
        }

        Util.print("Done %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());

        return false;
    }

    @Override
    protected void onError() {
        ChartInputDto chartInput = this.getInput();
        Util.error("ChartSearchService::onError: %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());
    }
}
