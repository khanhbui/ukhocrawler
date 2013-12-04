package com.kb.ukhocrawler.service.voa;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.service.RunnableService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class VoaAsItIsService extends RunnableService {

    private int totalPages;
    private String output;

    public VoaAsItIsService(int totalPages, String output) {
        this.totalPages = totalPages;
        this.output = output;
    }

    @Override
    protected void download() throws Exception {
        int currPage = 1;
        boolean hasPrev = true;

        while(currPage <= this.totalPages && hasPrev) {
            String url = String.format(Constant.VOA_AS_IT_IS_URL, currPage);
            Util.print("Fetching info from %s...", url);

            Document doc = Util.getConnection(url).get();
            if (doc != null) {
                Element divContainer = doc.getElementById("ctl00_ctl00_cpAB_cp1_repeaterListDivContent");

                Element divPaging = divContainer.getElementById("ctl00_ctl00_cpAB_cp1_upperInfoContent");
                Element prevElement = divPaging.select("span[class=prev]").first();
                Util.print("prev-page: %s", prevElement);
                hasPrev = (prevElement != null);

                Elements divArticles = divContainer.select("div[class=archive_rowmm]");
                for (Element article: divArticles) {
                    Element a = article.select("a[href]").first();
                    if (a != null) {
                        try {
                            this.getDetails(a.attr("href"));
                        } catch(Exception e0) {
                            Util.print("+++ Something's wrong. Retry #1.");
                            try {
                                this.getDetails(a.attr("href"));
                            } catch(Exception e1) {
                                Util.print("+++ Something's wrong. Retry #2.");
                                try {
                                    this.getDetails(a.attr("href"));
                                } catch(Exception e2) {
                                    Util.print("+++ Something's wrong. Retry #3.");
                                    try {
                                        this.getDetails(a.attr("href"));
                                    } catch(Exception e3) {
                                        throw e3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Util.print("Done info from %s...", url);
            currPage++;
        }
    }

    private void getDetails(String url) throws IOException {
        Util.print("Fetching details from %s...", Constant.VOA_URL + url);
        Document doc = Util.getConnection(Constant.VOA_URL + url).get();
        if (doc != null) {
            String title = doc.title();
            Element aPdf = doc.select("a[href$=.pdf]").first();
            if (aPdf != null) {
                Util.print("---Fetching pdf from %s...", aPdf.attr("href"));
                Util.print("------pdf: %s", aPdf.attr("href"));
                this.saveUrl(aPdf.attr("href"), this.output + "/transcript/" + title + ".pdf");
                Util.print("---Done pdf from %s...", aPdf.attr("href"));
            }

            Element aAudio = doc.select("a[class=listenico]").first();
            if (aAudio != null) {
                this.getAudio(Constant.VOA_URL + aAudio.attr("href"), title);
            }
        }
        Util.print("Done details from %s...", Constant.VOA_URL + url);
    }

    private void getAudio(String url, String title) throws IOException {
        Util.print("---Fetching audio from %s...", url);
        Document doc = Util.getConnection(url).get();
        if (doc != null) {
            Element aAudio = doc.select("a[class=downloadico]").first();
            if (aAudio != null) {
                Util.print("------audio: %s", aAudio.attr("href"));
                this.saveUrl(aAudio.attr("href"), this.output + "/audio/" + title + ".mp3");
            }
        }
        Util.print("---Done audio from %s...", url);
    }

    private void saveUrl(String url, String file) throws IOException {
        byte[] data = this.getDataFromUrl(url);

        Util.createDirs(file);

        FileOutputStream out = new FileOutputStream(file);
        out.write(data);
        out.close();
    }

    @Override
    protected void onError() {
        Util.error("VoaAsItIsService::onError: %s", Constant.VOA_AS_IT_IS_URL);
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
