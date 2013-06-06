package com.kb.ukhocrawler.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class InfoDriver extends PreviewDriver {

    public InfoDriver(ChartDto chart, String output) {
        super(chart, output);
    }

    @Override
    protected void download() throws IOException {
        if (chart.getInfoChartId().equals("")) {
            return;
        }

        String url = String.format(Constant.INFO_URL, chart.getInfoChartId());
        Util.print("Fetching info from %s...", url);

        Document doc = Util.getConnection(url).get();

        if (doc != null) {
            Element div = doc.select("div[class=box-content]").first();
            if (div != null) {
                String path = String.format(
                        Constant.INFO_PATH, 
                        output + File.separator +
                            chart.getChartType() + File.separator +
                            chart.getChartNumber() + File.separator +
                            chart.getInfoChartId());
                Util.createDirs(path);

                Util.print("Saving info to %s...", path);

                PrintStream out = null;
                try {
                    Util.createDirs(path);
                    out = new PrintStream(new FileOutputStream(path));
                    out.print(div.html());
                    Util.print("Done %s --> %s.", url, path);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
    }
}