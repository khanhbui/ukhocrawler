package com.kb.ukhocrawler;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.helper.Validate;

import com.kb.ukhocrawler.controller.Controller;
import com.kb.ukhocrawler.controller.AllChartsController;
import com.kb.ukhocrawler.controller.ChartController;
import com.kb.ukhocrawler.controller.PubController;
import com.kb.ukhocrawler.controller.VoaController;


public class App 
{
    public static void main(String[] args) throws IOException, InvalidFormatException
    {
        Validate.isTrue(args.length >= 3, "Usage: supply type (0:chart, 1: all charts, 2: pub, 3: voa), input file, output directory to fetch.");

        int type = Integer.parseInt(args[0]);
        Controller controller = null;
        switch (type) {
            case 0:
                controller = new ChartController();
                break;
            case 1:
                controller = new PubController();
                break;
            case 2:
                controller = new AllChartsController();
                break;
            case 3:
                controller = new VoaController();
                break;
            default:
                Validate.isTrue(false, "Usage: supply type (0:chart, 1: pub, 2: voa), input file, output directory to fetch.");
                break;
        }
        if (controller != null) {
            controller.start(args);
        }
    }
}
