package com.kb.ukhocrawler.driver.pub;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.driver.InfoDriver;
import com.kb.ukhocrawler.dto.pub.PubDto;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PubInfoDriver extends InfoDriver {

    public PubInfoDriver(PubDto pub) {
        super(pub);
    }

    @Override
    protected void download() throws IOException {
        PubDto pub = (PubDto)this.info;
        if (pub.getPubId() == null) {
            return;
        }

        String url = String.format(Constant.PUB_INFO_URL, pub.getPubId());
        Util.print("Fetching info from %s...", url);

        Document doc = Util.getConnection(url).get();

        if (doc != null) {
            Elements uls = doc.select("ul[class=chart-details]");
            if (uls != null) {
                for (int i = 0; i < uls.size(); ++i) {
                    Elements lis = uls.get(i).select("li");
                    switch (i) {
                        case 0:
                            for (int j = 0; j < lis.size(); ++j) {
                                String text = lis.get(j).text();
                                String str = Util.extract(text, "(.*)Number (.*)");
                                if (!str.equals("")) {
                                    pub.setNumber(str);
                                } else {
                                    str = Util.extract(text, "(.*)Title (.*)");
                                    if (!str.equals("")) {
                                        pub.setTitle(str);
                                    } else {
                                        str = Util.extract(text, "(.*)Sub Title (.*)");
                                        if (!str.equals("")) {
                                            pub.setSubTitle(str);
                                        } else {
                                            str = Util.extract(text, "(.*)Type (.*)");
                                            if (!str.equals("")) {
                                                pub.setType(str);
                                            } else {
                                                str = Util.extract(text, "(.*)SubType (.*)");
                                                if (!str.equals("")) {
                                                    pub.setSubType(str);
                                                } else {
                                                    str = Util.extract(text, "(.*)Edition No (.*)");
                                                    if (!str.equals("")) {
                                                        pub.setEditionNo(str);
                                                    } else {
                                                        str = Util.extract(text, "(.*)Pub Year (.*)");
                                                        if (!str.equals("")) {
                                                            pub.setPubYear(str);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Util.print("%s: %s: %s", text, str, pub);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        Util.print("Done %s.", url);
    }

    @Override
    protected void onError() {
        // TODO Auto-generated method stub
        
    }
}