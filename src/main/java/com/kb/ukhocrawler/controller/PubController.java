package com.kb.ukhocrawler.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.kb.ukhocrawler.dto.OutputDto;
import com.kb.ukhocrawler.dto.pub.PubInputDto;
import com.kb.ukhocrawler.dto.pub.PubDto;
import com.kb.ukhocrawler.dto.pub.PubSupplement;
import com.kb.ukhocrawler.service.IndexService;
import com.kb.ukhocrawler.service.pub.PubInfoService;
import com.kb.ukhocrawler.service.pub.PubSearchService;
import com.kb.ukhocrawler.utils.Util;

public class PubController {

    public PubController() {
    }

    public void start(String... args) throws IOException, InvalidFormatException
    {
        String input = args[1];
        String output = args[2];
        int connectionNum = args.length > 3 ? Integer.parseInt(args[3]) : 3;

        // submit the index page to retrieve cookie information
        IndexService index = new IndexService();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(connectionNum);
        List<PubSearchService> searchers = new ArrayList<PubSearchService>();
        Map<String, String> cookies = index.getCookies();
        List<String[]> list = this.getInput(input);
        for (String[] item: list) {
            PubSearchService searcher = new PubSearchService(cookies, new PubInputDto(item[0]), 0);
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
        for (PubSearchService searcher: searchers) {
            Util.print("Results: %s: %s", searcher.getInput().getPubNumber(), searcher.getResults());
            for (OutputDto pub: searcher.getResults()) {
                if (!flag.containsKey(pub.toString())) {
                    executor.execute(new PubInfoService((PubDto) pub));
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
        Collections.sort(pubs, new Comparator<PubDto>() {
            public int compare(PubDto a, PubDto b) {
                return a.getNumber().trim().compareToIgnoreCase(b.getNumber().trim());
            }
        });

        FileOutputStream out = new FileOutputStream(output);
        Workbook wb = new HSSFWorkbook();

        Sheet s1 = wb.createSheet("Publications");
        s1.createFreezePane(0, 1);
        Row r = s1.createRow(0);
        r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Number");
        r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Title");
        r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("Sub Title");
        r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Type");
        r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue("Sub Type");
        r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue("Edition No");
        r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue("Pub Year");

        Sheet s2 = wb.createSheet("Supplements");
        s2.createFreezePane(0, 1);
        r = s2.createRow(0);
        r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Pub. Number");
        r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Sup. Number");
        r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("Sup. Title");
        r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Sup. Year");
        r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue("Edition No");

        int j = 1;
        for (int i = 0; i < pubs.size(); ++i) {
            PubDto pub = pubs.get(i);

            r = s1.createRow(i + 1);
            r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(pub.getNumber());
            r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(pub.getTitle());
            r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(pub.getSubTitle());
            r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(pub.getType());
            r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(pub.getSubType());
            r.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(pub.getEditionNo());
            r.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(pub.getPubYear());

            for (PubSupplement sup: pub.getSupplements()) {
                r = s2.createRow(j++);
                r.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(pub.getNumber());
                r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(sup.getNumber());
                r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(sup.getTitle());
                r.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(sup.getYear());
                r.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(sup.getEditionNo());
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
