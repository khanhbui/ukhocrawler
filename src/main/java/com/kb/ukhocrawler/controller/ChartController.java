package com.kb.ukhocrawler.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.kb.ukhocrawler.dto.OutputDto;
import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.dto.chart.ChartInputDto;
import com.kb.ukhocrawler.dto.chart.PanelDto;
import com.kb.ukhocrawler.service.IndexService;
import com.kb.ukhocrawler.service.chart.ChartInfoService;
import com.kb.ukhocrawler.service.chart.ChartSearchService;
import com.kb.ukhocrawler.service.chart.ChartPreviewService;
import com.kb.ukhocrawler.utils.Util;

public class ChartController extends Controller {

    public ChartController() {
    }

    public void start(String[] args) throws IOException
    {
        String input = args[1];
        String output = args[2];
        String outputDir = new File(output).getParent();
        int connectionNum = args.length > 3 ? Integer.parseInt(args[3]) : 2;

        // submit the index page to retrieve cookie information
        IndexService index = new IndexService();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(connectionNum);
        List<ChartSearchService> searchers = new ArrayList<ChartSearchService>();
        Map<String, String> cookies = index.getCookies();
        List<String[]> list = this.getInput(input);
        for (String[] item: list) {
            ChartSearchService searcher = new ChartSearchService(cookies, new ChartInputDto(item[0], item[1], item[2]), 1);
            searchExecutor.execute(searcher);
            searchers.add(searcher);
        }
        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }

        // start retrieving info and image
        ExecutorService executor = Executors.newFixedThreadPool(connectionNum);
        Map<String, Boolean> flag = new HashMap<String, Boolean>();
        List<ChartDto> charts = new ArrayList<ChartDto>();
        for (ChartSearchService searcher: searchers) {
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

    public void save(String output, List<ChartDto> charts) throws IOException {
        FileOutputStream out = new FileOutputStream(output);
        Workbook wb = new HSSFWorkbook();

        Sheet s1 = wb.createSheet("Charts");
        s1.createFreezePane(0, 1);
        Row r = s1.createRow(0);
        r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Prefix");
        r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Chart Number");
        r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("Suffix");
        r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Chart Title");
        r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue("Publication Date");
        r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue("Latest Edition date");
        r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue("Chart Size");
        r.createCell(7, Cell.CELL_TYPE_STRING).setCellValue("Image");

        Sheet s2 = wb.createSheet("Panels");
        s2.createFreezePane(0, 1);
        r = s2.createRow(0);
        r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("ID");
        r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Prefix");
        r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("Chart Number");
        r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Suffix");
        r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue("Panel Name");
        r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue("Area Name");
        r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue("Natural Scale");
        r.createCell(7, Cell.CELL_TYPE_STRING).setCellValue("North Limit");
        r.createCell(8, Cell.CELL_TYPE_STRING).setCellValue("South Limit");
        r.createCell(9, Cell.CELL_TYPE_STRING).setCellValue("East Limit");
        r.createCell(10, Cell.CELL_TYPE_STRING).setCellValue("West Limit");

        int j = 1;
        for (int i = 0; i < charts.size(); ++i) {
            ChartDto chart = charts.get(i);

            r = s1.createRow(i + 1);
            r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(chart.getPrefix());
            r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(chart.getChartNumber());
            r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(chart.getSuffix());
            r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(chart.getChartTitle());
            r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(chart.getPublicationDate());
            r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(chart.getLatestEditionDate());
            r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(chart.getChartSize());
            r.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(chart.getImage());

            for(PanelDto panel: chart.getPanels()) {
                r = s2.createRow(j++);
                r.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(j);
                r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(chart.getPrefix());
                r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(chart.getChartNumber());
                r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(chart.getSuffix());
                r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(panel.getPanelName());
                r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(panel.getAreaName());
                r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(panel.getNaturalScale());
                r.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(panel.getNorthLimit());
                r.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(panel.getSouthLimit());
                r.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(panel.getEastLimit());
                r.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(panel.getWestLimit());
            }
        }

        wb.write(out);
        out.close();
    }

    public List<String[]> getInput(String file) throws IOException {
        List<String[]> list = new ArrayList<String[]>();

        Workbook workBook;
        FileInputStream fileInputStream = new FileInputStream(file);
        if (file.endsWith(".xlsx")) {
            workBook = new XSSFWorkbook(fileInputStream);
        }
        else {
            POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
            workBook = new HSSFWorkbook(fsFileSystem);
        }

        Sheet sheet = workBook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String[] item = new String[3];
            for (int i = 0; i < 3; ++i) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    item[i] = "";
                } else {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            item[i] = "";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            item[i] = Integer.toString((int) cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            item[i] = cell.getStringCellValue();
                            break;
                    }
                }
            }
            list.add(item);
        }

        fileInputStream.close();

        return list;
    }
}
