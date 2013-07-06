package com.kb.ukhocrawler.controller;

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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.helper.Validate;

import com.kb.ukhocrawler.driver.IndexDriver;
import com.kb.ukhocrawler.driver.pub.PubInfoDriver;
import com.kb.ukhocrawler.driver.pub.PubSearchDriver;
import com.kb.ukhocrawler.dto.OutputDto;
import com.kb.ukhocrawler.dto.pub.PubInputDto;
import com.kb.ukhocrawler.dto.pub.PubDto;
import com.kb.ukhocrawler.utils.Util;

public class PubController {

    public PubController() {
    }

    public void start(String[] args) throws IOException, InvalidFormatException
    {
        Validate.isTrue(args.length >= 2, "Usage: supply input file, output directory to fetch.");
        String input = args[0];
        String output = args[1];
        int connectionNum = args.length > 2 ? Integer.parseInt(args[2]) : 3;

        // submit the index page to retrieve cookie information
        IndexDriver index = new IndexDriver();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(connectionNum);
        List<PubSearchDriver> searchers = new ArrayList<PubSearchDriver>();
        Map<String, String> cookies = index.getCookies();
        List<String[]> list = this.getInput(input);
        for (String[] item: list) {
            PubSearchDriver searcher = new PubSearchDriver(cookies, new PubInputDto(item[0]), 1);
            searchExecutor.execute(searcher);
            searchers.add(searcher);
        }
        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }

        // start retrieving info and image
        ExecutorService executor = Executors.newFixedThreadPool(connectionNum);
        Map<String, Boolean> flag = new HashMap<String, Boolean>();
        List<PubDto> pubs = new ArrayList<PubDto>();
        for (PubSearchDriver searcher: searchers) {
            Util.print("Results: %s: %s", searcher.getInput().getPubNumber(), searcher.getResults());
            for (OutputDto pub: searcher.getResults()) {
                if (!flag.containsKey(pub.toString())) {
                    executor.execute(new PubInfoDriver((PubDto) pub));
                    pubs.add((PubDto) pub);
                    flag.put(pub.toString(), true);
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        // save to file
        this.save(output, pubs);

        Util.print("Finished all. (tung hoa, tung hoa, tung hoa)");
    }

    public void save(String output, List<PubDto> pubs) throws IOException {
        FileOutputStream out = new FileOutputStream(output);
        Workbook wb = new HSSFWorkbook();

        Sheet s1 = wb.createSheet("Charts");
        Row r = s1.createRow(0);
        r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Number");
        r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Title");
        r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("Sub Title");
        r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Type");
        r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue("Sub Type");
        r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue("Edition No");
        r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue("Pub Year");

        for (int i = 0; i < pubs.size(); ++i) {
            PubDto pub = pubs.get(i);
            Util.print("%s", pub);

            r = s1.createRow(i + 1);
            r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(pub.getNumber());
            r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(pub.getTitle());
            r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(pub.getSubTitle());
            r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(pub.getType());
            r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(pub.getSubType());
            r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(pub.getEditionNo());
            r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(pub.getPubYear());
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
            for (int i = 0; i < 1; ++i) {
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
