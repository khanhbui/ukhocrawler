package com.kb.ukhocrawler;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.helper.Validate;

import com.kb.ukhocrawler.controller.AllChartsController;
import com.kb.ukhocrawler.controller.ChartController;
import com.kb.ukhocrawler.controller.PubController;


public class App 
{
    public static void main(String[] args) throws IOException, InvalidFormatException
    {
        Validate.isTrue(args.length >= 3, "Usage: supply type (0:chart, 1: all charts, 2: pub), input file, output directory to fetch.");

        int type = Integer.parseInt(args[0]);
        switch (type) {
            case 0:
                new ChartController().start(args);
                break;
            case 1:
            	new AllChartsController().start(args);
                break;
            default:
                new PubController().start(args);
                break;
        }
    }
}
