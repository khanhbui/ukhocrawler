package com.kb.ukhocrawler.driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.kb.ukhocrawler.dto.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PreviewDriver implements Runnable {

    protected List<ChartDto> charts;

    public PreviewDriver(List<ChartDto> charts) {
        this.charts = charts;
    }

    public void run() {
        for (ChartDto chart: charts) {
            try {
                download(chart);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void download(ChartDto chart) throws IOException {
        if (chart.getPreviewChartId().equals("")) {
            return;
        }

        String url = String.format(Constant.PREVIEW_URL, chart.getPreviewChartId());
        Util.print("Fetching image from %s...", url);
        byte [] image = getDataFromUrl(url);

        if (image != null){
            String path = String.format(
                    Constant.PREVIEW_PATH,
                    File.separator, 
                    File.separator +
                        chart.getChartType() + File.separator +
                        chart.getChartNumber() + File.separator +
                        chart.getPreviewChartId());
            Util.createDirs(path);

            Util.print("Saving image to %s...", path);
            FileOutputStream out = new FileOutputStream(path);
            out.write(image);
            out.close();
            Util.print("Done %s --> %s.", url, path);
        }
    }

    public static byte [] getDataFromUrl(String url) throws IOException{
        return loadBytesFromURL(new URL(url));
    }

    private static byte[] loadBytesFromURL(URL url) throws IOException {
        byte[] b = null;
        URLConnection con = url.openConnection();
        int size = con.getContentLength();

        InputStream in = null;

        try {
            if ((in = con.getInputStream()) != null) {
                b = (size != -1) ? loadBytesFromStreamForSize(in, size) : loadBytesFromStream(in);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return b;
      }

    public static byte[] loadBytesFromStreamForSize(InputStream in, int size) throws IOException {
        int count, index = 0;
        byte[] b = new byte[size];

        // read in the bytes from input stream
        while((count = in.read(b, index, size)) > 0) {
            size -= count;
            index += count;
        }
        return b;
    }

    public static  byte[] loadBytesFromStream(InputStream in) throws IOException {
        return loadBytesFromStream(in, Constant.DEFAULT_CHUNK_SIZE);
    }

    private static byte[] loadBytesFromStream(InputStream in, int chunkSize) throws IOException {
        if (chunkSize < 1) {
            chunkSize = Constant.DEFAULT_CHUNK_SIZE;
        }

        int count;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] b = new byte[chunkSize];
        try {
            while((count = in.read(b, 0, chunkSize)) > 0) {
                bo.write(b, 0, count);
            }
            byte[] thebytes = bo.toByteArray();
            return thebytes;
        } finally {
            bo.close();
            bo = null;
        }
    }
}
