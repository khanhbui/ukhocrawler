package com.kb.ukhocrawler;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.kb.ukhocrawler.controller.ChartController;
import com.kb.ukhocrawler.controller.PubController;


public class App 
{
    public static void main( String[] args ) throws IOException, InvalidFormatException
    {
        if (args.length > 10) {
            new ChartController().start(args);
        } else {
            new PubController().start(args);
        }
    }
}
