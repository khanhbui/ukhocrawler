package com.kb.ukhocrawler;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.kb.ukhocrawler.controller.ChartController;


public class App 
{
    public static void main( String[] args ) throws IOException, InvalidFormatException
    {
        new ChartController().start(args);
        
    }
}
