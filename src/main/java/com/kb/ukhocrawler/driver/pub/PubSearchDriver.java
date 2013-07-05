package com.kb.ukhocrawler.driver.pub;

import java.io.IOException;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.driver.SearchDriver;
import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.pub.PubInputDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PubSearchDriver extends SearchDriver {

    public PubSearchDriver(Map<String, String> cookies, PubInputDto input, int searchMethod) {
        super(cookies, input, searchMethod);
    }

    @Override
    public PubInputDto getInput() {
        return (PubInputDto)input;
    }

    @Override
    protected void download() throws IOException {
        PubInputDto pubInput = this.getInput();

        Util.print("Fetching %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());

        Document doc = Util.getConnection(Constant.SEARCH_BOOK_URL)
                .cookies(cookies)
                .data(Constant.PUB_NUMBER, pubInput.getPubNumber())
                .post();

        Elements tbodies = doc.getElementsByTag("tbody");
        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");

                ChartDto chart = new ChartDto();
                chart.setPrefix(pubInput.getPubNumber());
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
                    if (str.contains(pubInput.getPubNumber())) {
                        results.add(chart);
                        break;
                    }
                } else {
                    results.add(chart);
                }
            }
        }

        Util.print("Done %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());
    }

    @Override
    protected void onError() {
        PubInputDto pubInput = this.getInput();
        Util.error("Error: %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());
    }
}
