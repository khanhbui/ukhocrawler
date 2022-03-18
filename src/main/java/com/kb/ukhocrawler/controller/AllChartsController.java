package com.kb.ukhocrawler.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.kb.ukhocrawler.dto.OutputDto;
import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.chart.ChartInputDto;
import com.kb.ukhocrawler.dto.chart.PanelDto;
import com.kb.ukhocrawler.service.IndexService;
import com.kb.ukhocrawler.service.chart.AllChartSearchService;
import com.kb.ukhocrawler.service.chart.ChartInfoService;
import com.kb.ukhocrawler.service.chart.ChartPreviewService;
import com.kb.ukhocrawler.utils.Util;

public class AllChartsController extends Controller {

    public AllChartsController() {
    }

    public void start(String[] args) throws IOException
    {
        String output = args[2];
        String outputDir = new File(output).getParent();

        // submit the index page to retrieve cookie information
        IndexService index = new IndexService();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(1);
        List<AllChartSearchService> searchers = new ArrayList<AllChartSearchService>();
        Map<String, String> cookies = index.getCookies();
        for (int i = 0; i < 10; ++i) {
        	AllChartSearchService searcher = new AllChartSearchService(cookies, new ChartInputDto("", Integer.toString(i), ""));
        	searchExecutor.execute(searcher);
        	searchers.add(searcher);
        }
        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }

        // start retrieving info and image
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Map<String, Boolean> flag = new HashMap<String, Boolean>();
        List<ChartDto> charts = new ArrayList<ChartDto>();
        for (AllChartSearchService searcher: searchers) {
            Util.print("Results: %s %s %s: %s", searcher.getInput().getChartPrefix(), searcher.getInput().getChartNumber(), searcher.getInput().getChartSuffix(), searcher.getResults());
            for (OutputDto chart: searcher.getResults()) {
                if (!flag.containsKey(chart.toString())) {
                    executor.execute(new ChartPreviewService((ChartDto) chart, outputDir));
                    executor.execute(new ChartInfoService((ChartDto) chart));
                    charts.add((ChartDto) chart);
                    flag.put(chart.toString(), true);
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        // save to file
        this.save(output, charts);

        Util.print("Finished all. (tung hoa, tung hoa, tung hoa)");
    }

    private int compareString(String a, String b) {
    	int la = a.length();
    	int lb = b.length();
    	if (la < lb) {
    		return -1;
    	} else if (la > lb) {
    		return 1;
    	} else {
	    	for (int i = 0; i < la; ++i) {
	    		if (a.charAt(i) < b.charAt(i)) {
	    			return -1;
	    		} else if (a.charAt(i) > b.charAt(i)) {
	    			return 1;
	    		}
	    	}
	    	return 0;
    	}
    }

    public void save(String output, List<ChartDto> charts) throws IOException {
    	Collections.sort(charts, new Comparator<ChartDto>() {
            public int compare(ChartDto a, ChartDto b) {
            	String numberA = a.getChartNumber().trim().toLowerCase();
            	String numberB = b.getChartNumber().trim().toLowerCase();
            	if (compareString(numberA, numberB) == 0) {
            		String prefixA = a.getPrefix().trim().toLowerCase();
            		String prefixB = b.getPrefix().trim().toLowerCase();
            		if (compareString(prefixA, prefixB) == 0) {
            			String suffixA = a.getSuffix().trim().toLowerCase();
                		String suffixB = b.getSuffix().trim().toLowerCase();
                		return compareString(suffixA, suffixB);
            		} else {
            			return compareString(prefixA, prefixB);
            		}
            	} else {
            		return compareString(numberA, numberB);
            	}
            }
        });

        FileOutputStream out = new FileOutputStream(output);
        Workbook wb = new HSSFWorkbook();

        Sheet s1 = wb.createSheet("Charts");
        s1.createFreezePane(0, 1);
        Row r = s1.createRow(0);
        r.createCell(0, CellType.STRING).setCellValue("Prefix");
        r.createCell(1, CellType.STRING).setCellValue("Chart Number");
        r.createCell(2, CellType.STRING).setCellValue("Suffix");
        r.createCell(3, CellType.STRING).setCellValue("Chart Title");
        r.createCell(4, CellType.STRING).setCellValue("Publication Date");
        r.createCell(5, CellType.STRING).setCellValue("Latest Edition date");
        r.createCell(6, CellType.STRING).setCellValue("Chart Size");
        r.createCell(7, CellType.STRING).setCellValue("Image");

        Sheet s2 = wb.createSheet("Panels");
        s2.createFreezePane(0, 1);
        r = s2.createRow(0);
        r.createCell(0, CellType.STRING).setCellValue("ID");
        r.createCell(1, CellType.STRING).setCellValue("Prefix");
        r.createCell(2, CellType.STRING).setCellValue("Chart Number");
        r.createCell(3, CellType.STRING).setCellValue("Suffix");
        r.createCell(4, CellType.STRING).setCellValue("Panel Name");
        r.createCell(5, CellType.STRING).setCellValue("Area Name");
        r.createCell(6, CellType.STRING).setCellValue("Natural Scale");
        r.createCell(7, CellType.STRING).setCellValue("North Limit");
        r.createCell(8, CellType.STRING).setCellValue("South Limit");
        r.createCell(9, CellType.STRING).setCellValue("East Limit");
        r.createCell(10, CellType.STRING).setCellValue("West Limit");

        int j = 1;
        for (int i = 0; i < charts.size(); ++i) {
            ChartDto chart = charts.get(i);

            r = s1.createRow(i + 1);
            r.createCell(0, CellType.STRING).setCellValue(chart.getPrefix());
            r.createCell(1, CellType.STRING).setCellValue(chart.getChartNumber());
            r.createCell(2, CellType.STRING).setCellValue(chart.getSuffix());
            r.createCell(3, CellType.STRING).setCellValue(chart.getChartTitle());
            r.createCell(4, CellType.STRING).setCellValue(chart.getPublicationDate());
            r.createCell(5, CellType.STRING).setCellValue(chart.getLatestEditionDate());
            r.createCell(6, CellType.STRING).setCellValue(chart.getChartSize());
            r.createCell(7, CellType.STRING).setCellValue(chart.getImage());

            for(PanelDto panel: chart.getPanels()) {
                r = s2.createRow(j++);
                r.createCell(0, CellType.NUMERIC).setCellValue(j);
                r.createCell(1, CellType.STRING).setCellValue(chart.getPrefix());
                r.createCell(2, CellType.STRING).setCellValue(chart.getChartNumber());
                r.createCell(3, CellType.STRING).setCellValue(chart.getSuffix());
                r.createCell(4, CellType.STRING).setCellValue(panel.getPanelName());
                r.createCell(5, CellType.STRING).setCellValue(panel.getAreaName());
                r.createCell(6, CellType.STRING).setCellValue(panel.getNaturalScale());
                r.createCell(7, CellType.STRING).setCellValue(panel.getNorthLimit());
                r.createCell(8, CellType.STRING).setCellValue(panel.getSouthLimit());
                r.createCell(9, CellType.STRING).setCellValue(panel.getEastLimit());
                r.createCell(10, CellType.STRING).setCellValue(panel.getWestLimit());
            }
        }

        wb.write(out);
        out.close();
    }
}
