package com.kb.ukhocrawler.service.pub;

import java.io.IOException;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.pub.PubDto;
import com.kb.ukhocrawler.dto.pub.PubInputDto;
import com.kb.ukhocrawler.service.SearchService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PubSearchService extends SearchService {

    public PubSearchService(Map<String, String> cookies, PubInputDto input, int searchMethod) {
        super(cookies, input, searchMethod);
    }

    @Override
    public PubInputDto getInput() {
        return (PubInputDto)input;
    }

    @Override
    protected void download() throws IOException {
        PubInputDto pubInput = this.getInput();

        Util.print("Fetching %s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());

        Document doc = Util.getConnection(Constant.SEARCH_BOOK_URL)
                .cookies(cookies)
                .data(Constant.PUB_NUMBER, pubInput.getPubNumber())
                .post();

        Elements tbodies = doc.getElementsByTag("tbody");
        this.extractTbodies(tbodies);

        String nextPage = null;
        while ((nextPage = this.nextPage(doc)) != null) {
            doc = Util.getConnection(Constant.MAIN_URL + nextPage)
                    .cookies(cookies)
                    .get();
            tbodies = doc.getElementsByTag("tbody");
            this.extractTbodies(tbodies);
            Util.print("----- Done %s", Constant.MAIN_URL + nextPage);
        }

        Util.print("Done %s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());
    }

    @Override
    protected void onError() {
        PubInputDto pubInput = this.getInput();
        Util.error("PubSearchService::onError: %s&%s=%s", Constant.SEARCH_BOOK_URL, Constant.PUB_NUMBER, pubInput.getPubNumber());
    }

    private String nextPage(Document doc) {
        //<a href="browse_PUBS_results.asp?FilterMethod=1&amp;PubNumber=1&amp;offset=10" title="Next 10" in_tag="ul" kaspersky_status="skipped">Next 10</a>
        Element a = doc.select("a[title=Next 10]").first();
        return a == null ? null : a.attr("href");
    }

    private void extractTbodies(Elements tbodies) {
        PubInputDto pubInput = this.getInput();

        for (Element tbody : tbodies) {
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");

                PubDto pub = new PubDto();
                String str = "";
                for (int i = 0; i < tds.size(); ++i) {
                    Element td = tds.get(i);
                    switch (i) {
                        case 0:
                            str = td.text();
                            pub.setNumber(str);
                            break;
                        case 2:
                            Element a = td.getElementsByTag("a").first();
                            pub.setPubId(a == null ? "" : Util.extract(a.attr("href"), "(.*)PubID=(\\d+)", 2));
                            break;
                        default:
                            break;
                    }
                }
                if (searchMethod == 1) {
                    if (str.equals(pubInput.getPubNumber())) {
                        results.add(pub);
                        break;
                    }
                } else {
                    results.add(pub);
                }
            }
        }
    }
}
