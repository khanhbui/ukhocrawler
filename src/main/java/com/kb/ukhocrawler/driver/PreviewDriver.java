package com.kb.ukhocrawler.driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.kb.ukhocrawler.dto.chart.ChartDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PreviewDriver extends RunnableDriver {

    protected ChartDto chart;
    protected String output;

    public PreviewDriver(ChartDto chart, String output) {
        this.chart = chart;
        this.output = output;
    }

    @Override
    protected void download() throws IOException {
        if (chart.getPreviewChartId().equals("")) {
            return;
        }

        String url = String.format(Constant.PREVIEW_URL, chart.getPreviewChartId());
        Util.print("Fetching image from %s...", url);
        chart.setImage(url);
        byte [] image = getDataFromUrl(url);

        if (image != null){
            String path = String.format(
                    Constant.PREVIEW_PATH, 
                    output + File.separator +
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

    @Override
    protected void onError() {
    }

    private byte [] getDataFromUrl(String url) throws IOException{
        return loadBytesFromURL(new URL(url));
    }

    private byte[] loadBytesFromURL(URL url) throws IOException {
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

    private byte[] loadBytesFromStreamForSize(InputStream in, int size) throws IOException {
        int count, index = 0;
        byte[] b = new byte[size];

        // read in the bytes from input stream
        while((count = in.read(b, index, size)) > 0) {
            size -= count;
            index += count;
        }
        return b;
    }

    private byte[] loadBytesFromStream(InputStream in) throws IOException {
        return loadBytesFromStream(in, Constant.DEFAULT_CHUNK_SIZE);
    }

    private byte[] loadBytesFromStream(InputStream in, int chunkSize) throws IOException {
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
