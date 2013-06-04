package com.kb.ukhocrawler;

import java.io.IOException;
import org.jsoup.helper.Validate;
import com.kb.ukhocrawler.driver.IndexDriver;
import com.kb.ukhocrawler.driver.InfoDriver;
import com.kb.ukhocrawler.driver.PreviewDriver;
import com.kb.ukhocrawler.driver.SearchDriver;
import com.kb.ukhocrawler.utils.Util;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Validate.isTrue(args.length == 3, "usage: supply Chart Prefix, Chart Number, Chart Suffix to fetch");
        String chartPrefix = "";
        String chartNumber = args[1];
        String chartSuffix = "";

        IndexDriver index = new IndexDriver();
        index.submit();

        SearchDriver search = new SearchDriver();
        search.submit(index.getCookies(), chartPrefix, chartNumber, chartSuffix);
        search.save();

        Util.print("%s", search.getCharts());
        Thread gdt1 = new Thread(new PreviewDriver(search.getCharts()));
        gdt1.start();
        Thread gdt2 = new Thread(new InfoDriver(search.getCharts()));
        gdt2.start();
    }
}
