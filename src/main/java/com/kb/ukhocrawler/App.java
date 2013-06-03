package com.kb.ukhocrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Validate.isTrue(args.length == 1, "usage: supply Chart Number to fetch");
        String chartNumber = args[0];

        submitIndex(chartNumber);
    }

    private static void submitIndex(String chartNumber) throws IOException {
        String url = "http://www.ukho.gov.uk/onlinecatalogue/index.asp?UserType=commercial&SaveSettings=no&GraphicSet=full&ReferringURL=&Submit=Enter+site";
        print("Fetching %s...", url);

        Connection con = Jsoup.connect(url);
        con.userAgent("Mozilla").get();

        Map<String, String> cookies = con.request().cookies();
        print("\nCookies: %s", cookies.toString());

        submitSearch(cookies, chartNumber);
    }

    private static void submitSearch(Map<String, String> cookies, String chartNumber) throws IOException {
        String url = "http://www.ukho.gov.uk/onlinecatalogue/browse_SNCs_results.asp?FilterMethod=1";
        print("Fetching %s...", url);

        Document doc = Jsoup
                .connect(url)
                .userAgent("Mozilla")
                .cookies(cookies)
                .data("ChartPrefix", "")
                .data("ChartNumber", chartNumber)
                .data("ChartSuffix", "")
                .post();

        Elements tbodies = doc.getElementsByTag("tbody");
        print("\ntbody: %d", tbodies.size());

        List<ChartDto> charts = new ArrayList<ChartDto>();
        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            print("%s", trs.html());
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
                            chart.setChartInfoId(a == null ? "" : extract(a.attr("href"), "(.*)ChartID=(\\d+)"));
                            break;
                        case 5:
                            a = td.getElementsByTag("a").first();
                            chart.setChartPreviewId(a == null ? "" : extract(a.attr("href"), "(.*)ChartID=(\\d+)(.*)"));
                            break;
                        default:
                            break;
                    }
                }
                charts.add(chart);
            }
        }
        print("Results: %s", charts);
    }

    private static String extract(String str, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);

        if (m.find()) {
           return m.group(2);
        } else {
           return "";
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
