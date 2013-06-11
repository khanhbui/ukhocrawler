package com.kb.ukhocrawler;

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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.helper.Validate;
import com.kb.ukhocrawler.driver.IndexDriver;
import com.kb.ukhocrawler.driver.InfoDriver;
import com.kb.ukhocrawler.driver.PreviewDriver;
import com.kb.ukhocrawler.driver.SearchDriver;
import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.dto.PanelDto;
import com.kb.ukhocrawler.utils.Util;


public class App 
{
    public static void main( String[] args ) throws IOException, InvalidFormatException
    {
        Validate.isTrue(args.length == 2, "Usage: supply input file, output directory to fetch.");
        String input = args[0];
        String output = args[1];
        String outputDir = new File(output).getParent();

        // submit the index page to retrieve cookie information
        IndexDriver index = new IndexDriver();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(5);
        List<SearchDriver> searchers = new ArrayList<SearchDriver>();
        Map<String, String> cookies = index.getCookies();
        List<String[]> list = App.getInput(input);
        for (String[] item: list) {
            SearchDriver searcher = new SearchDriver(cookies, item[0], item[1], item[2], 1);
            searchExecutor.execute(searcher);
            searchers.add(searcher);
        }
        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }

        // start retrieving info and image
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Map<String, Boolean> flag = new HashMap<String, Boolean>();
        List<ChartDto> charts = new ArrayList<ChartDto>();
        for (SearchDriver searcher: searchers) {
            Util.print("Results: %s %s %s: %s", searcher.getChartPrefix(), searcher.getChartNumber(), searcher.getChartSuffix(), searcher.getResults());
            for (ChartDto chart: searcher.getResults()) {
                if (!flag.containsKey(chart.toString())) {
                    executor.execute(new PreviewDriver(chart, outputDir));
                    executor.execute(new InfoDriver(chart));
                    charts.add(chart);
                    flag.put(chart.toString(), true);
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        // save to file
        App.save(output, charts);

        Util.print("Finished all. (tung hoa, tung hoa, tung hoa)");
    }

    public static void save(String output, List<ChartDto> charts) throws IOException {
        FileOutputStream out = new FileOutputStream(output);
        Workbook wb = new HSSFWorkbook();

        Sheet s1 = wb.createSheet("Charts");
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

        int n = 1;
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

            List<PanelDto> panels = chart.getPanels();
            for(int j = 0; j < panels.size(); ++j) {
                PanelDto panel = panels.get(j);

                r = s2.createRow(n++);
                r.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(n);
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

    public static List<String[]> getInput(String file) throws IOException {
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
