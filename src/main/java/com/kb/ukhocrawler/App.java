package com.kb.ukhocrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.helper.Validate;
import com.kb.ukhocrawler.driver.IndexDriver;
import com.kb.ukhocrawler.driver.InfoDriver;
import com.kb.ukhocrawler.driver.PreviewDriver;
import com.kb.ukhocrawler.driver.SearchDriver;
import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Validate.isTrue(args.length == 2, "Usage: supply input file, output directory to fetch.");
        String input = args[0];
        String output = args[1];

        // submit the index page to retrieve cookie information
        IndexDriver index = new IndexDriver();
        index.submit();

        // start retrieving chart information
        ExecutorService searchExecutor = Executors.newFixedThreadPool(5);
        List<SearchDriver> searchers = new ArrayList<SearchDriver>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(input));
            String chartNumber;
            while ((chartNumber = br.readLine()) != null) {
                SearchDriver searcher = new SearchDriver(index.getCookies(), "", chartNumber, "");
                searchExecutor.execute(searcher);
                searchers.add(searcher);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }

        // start retrieving info and image
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Map<String, Boolean> flag = new HashMap<String, Boolean>();
        for (SearchDriver searcher: searchers) {
            Util.print("Results: %s: %s", searcher.getChartNumber(), searcher.getResults());
            for (ChartDto chart: searcher.getResults()) {
                if (!flag.containsKey(chart.toString())) {
                    executor.execute(new PreviewDriver(chart, output));
                    executor.execute(new InfoDriver(chart, output));
                    flag.put(chart.toString(), true);
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        Util.print("Finished all. (tung hoa, tung hoa, tung hoa)");
    }

    public void save() throws IOException {
        String path = String.format(Constant.GENERAL_INFO_PATH, File.separator, File.separator);
        PrintStream out = null;
        try {
            Util.createDirs(path);
            out = new PrintStream(new FileOutputStream(path));
            out.print("");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
