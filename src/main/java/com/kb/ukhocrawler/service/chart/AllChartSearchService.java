package com.kb.ukhocrawler.service.chart;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.chart.ChartInputDto;
import com.kb.ukhocrawler.service.SearchService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class AllChartSearchService extends SearchService {

	public AllChartSearchService(Map<String, String> cookies, ChartInputDto chartInputDto) {
		super(cookies, chartInputDto, 0);
	}

	@Override
	public ChartInputDto getInput() {
		return (ChartInputDto)input;
	}

	@Override
	protected void download() throws Exception {
		ChartInputDto chartInput = this.getInput();

        Util.print("Fetching %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());

        Document doc;
        try {
	        doc = Util.getConnection(Constant.SEARCH_URL)
                .cookies(cookies)
                .data(Constant.CHART_PREFIX, chartInput.getChartPrefix())
                .data(Constant.CHART_NUMBER, chartInput.getChartNumber())
                .data(Constant.CHART_SUFFIX, chartInput.getChartSuffix())
                .post();
        } catch(Exception e1) {
	    	try {
		        doc = Util.getConnection(Constant.SEARCH_URL)
	                .cookies(cookies)
	                .data(Constant.CHART_PREFIX, chartInput.getChartPrefix())
	                .data(Constant.CHART_NUMBER, chartInput.getChartNumber())
	                .data(Constant.CHART_SUFFIX, chartInput.getChartSuffix())
	                .post();
	    	} catch(Exception e2) {
		    	try {
			        doc = Util.getConnection(Constant.SEARCH_URL)
		                .cookies(cookies)
		                .data(Constant.CHART_PREFIX, chartInput.getChartPrefix())
		                .data(Constant.CHART_NUMBER, chartInput.getChartNumber())
		                .data(Constant.CHART_SUFFIX, chartInput.getChartSuffix())
		                .post();
		    	} catch(Exception e3) {
			    	throw e3;
			    }
		    }
	    }

        if (doc != null) {
	        Elements tbodies = doc.getElementsByTag("tbody");
	        Boolean found = this.extractTbodies(tbodies);

	        String nextPage = null;
	        while (!found && (nextPage = this.nextPage(doc)) != null) {
	        	try {
	        		doc = Util.getConnection(Constant.MAIN_URL + nextPage).cookies(cookies).get();
	        	} catch(Exception e1) {
	        		try {
		        		doc = Util.getConnection(Constant.MAIN_URL + nextPage).cookies(cookies).get();
		        	} catch(Exception e2) {
		        		try {
			        		doc = Util.getConnection(Constant.MAIN_URL + nextPage).cookies(cookies).get();
			        	} catch(Exception e3) {
			        		throw e3;
			        	}
		        	}
	        	}
	            tbodies = doc.getElementsByTag("tbody");
	            found = this.extractTbodies(tbodies);
	            Util.print("----- Done %s", Constant.MAIN_URL + nextPage);
	        }
        }
    }

    private Boolean extractTbodies(Elements tbodies) {
        ChartInputDto chartInput = this.getInput();

        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");

                ChartDto chart = new ChartDto();
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
                String chartPrefix = this.getPrefix(str);
                String chartNumber = this.getNumber(str);
                String chartSuffix = this.getSuffix(str);
                Util.print("-------------- |%s| = |%s| + |%s| + |%s|", str, chartPrefix, chartNumber, chartSuffix);
                chart.setPrefix(chartPrefix);
                chart.setChartNumber(chartNumber);
                chart.setSuffix(chartSuffix);
                results.add(chart);
            }
        }

        Util.print("Done %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());

        return false;
    }

    private String getSuffix(String str) {
    	String suffix = Util.extract(str, "^([a-zA-Z]*)(\\d+)([\\(]*)(\\w*)([\\)]*)", 4);
    	return suffix;
	}

	private String getNumber(String str) {
    	String number = Util.extract(str, "^([a-zA-Z]*)(\\d+)(.*)", 2);
    	return number;
	}

	private String getPrefix(String str) {
    	String list[] = {"AUS", "B", "C", "D", "JP", "NZ", "Q", "S", "X"};
    	for (int i = 0; i < list.length; ++i) {
    		String prefix = Util.extract(str, "^(" + list[i] + ")(\\d+)(.*)", 1);
    		if (prefix != null && prefix != "") {
    			return prefix;
    		}
    	}
    	return "";
	}

	@Override
    protected void onError() {
        ChartInputDto chartInput = this.getInput();
        Util.error("ChartSearchService::onError: %s&%s=%s&%s=%s&%s=%s", Constant.SEARCH_URL, Constant.CHART_PREFIX, chartInput.getChartPrefix(), Constant.CHART_NUMBER, chartInput.getChartNumber(), Constant.CHART_SUFFIX, chartInput.getChartSuffix());
    }
}
