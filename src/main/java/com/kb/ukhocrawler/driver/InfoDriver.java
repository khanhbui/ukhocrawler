package com.kb.ukhocrawler.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class InfoDriver extends PreviewDriver {

    public InfoDriver(List<ChartDto> charts) {
        super(charts);
    }

    protected void download(ChartDto chart) throws IOException {
        if (chart.getInfoChartId().equals("")) {
            return;
        }

        String url = String.format(Constant.INFO_URL, chart.getInfoChartId());
        Util.print("Fetching info from %s...", url);
        byte [] info = getDataFromUrl(url);

        if (info != null){
            String path = String.format(
                    Constant.INFO_PATH,
                    File.separator, 
                    File.separator +
                        chart.getChartType() + File.separator +
                        chart.getChartNumber() + File.separator +
                        chart.getInfoChartId());
            Util.createDirs(path);

            Util.print("Saving info to %s...", path);
            FileOutputStream out = new FileOutputStream(path);
            out.write(info);
            out.close();
            Util.print("Done %s --> %s.", url, path);
        }
    }
}