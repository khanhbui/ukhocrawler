package com.kb.ukhocrawler.service.pub;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kb.ukhocrawler.dto.pub.PubDto;
import com.kb.ukhocrawler.dto.pub.PubSupplement;
import com.kb.ukhocrawler.service.InfoService;
import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class PubInfoService extends InfoService {

    public PubInfoService(PubDto pub) {
        super(pub);
    }

    @Override
    public PubDto getInfo() {
        return (PubDto) this.info;
    }

    @Override
    protected void download() throws IOException {
        PubDto pub = this.getInfo();
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
                                String str = Util.extract(text, "(.*)Number (.*)", 2);
                                if (!str.equals("")) {
                                    pub.setNumber(str);
                                } else {
                                    str = Util.extract(text, "(.*)Title (.*)", 2);
                                    if (!str.equals("")) {
                                        String subStr = Util.extract(text, "(.*)Sub Title (.*)", 2);
                                        if (subStr.equals("")) {
                                            pub.setTitle(str);
                                        } else {
                                            pub.setSubTitle(subStr);
                                        }
                                    } else {
                                        str = Util.extract(text, "(.*)Type (.*)", 2);
                                        if (!str.equals("")) {
                                            String subStr = Util.extract(text, "(.*)SubType (.*)", 2);
                                            if (subStr.equals("")) {
                                                pub.setType(str);
                                            } else {
                                                pub.setSubType(str);
                                            }
                                        } else {
                                            str = Util.extract(text, "(.*)Edition No (.*)", 2);
                                            if (!str.equals("")) {
                                                pub.setEditionNo(str);
                                            } else {
                                                str = Util.extract(text, "(.*)Pub Year (.*)", 2);
                                                if (!str.equals("")) {
                                                    pub.setPubYear(str);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            Util.print("------------- Publication Supplement Details: %s", uls.get(i).text());

                            PubSupplement sup = new PubSupplement();
                            boolean hasSup = false;
                            for (int j = 0; j < lis.size(); ++j) {
                                String text = lis.get(j).text();
                                String str = Util.extract(text, "(.*)Sup. Number (.*)", 2);
                                if (!str.equals("")) {
                                    sup.setNumber(str);
                                    hasSup = true;
                                } else {
                                    str = Util.extract(text, "(.*)Sup. Title (.*)", 2);
                                    if (!str.equals("")) {
                                        sup.setTitle(str);
                                        hasSup = true;
                                    } else {
                                        str = Util.extract(text, "(.*)Sup. Year (.*)", 2);
                                        if (!str.equals("")) {
                                            sup.setYear(str);
                                            hasSup = true;
                                        } else {
                                            str = Util.extract(text, "(.*)Edition No (.*)", 2);
                                            if (!str.equals("")) {
                                                sup.setEditionNo(str);
                                                hasSup = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasSup) {
                                pub.addSup(sup);
                            }
                            break;
                    }
                }
            }
        }
        Util.print("Done %s. %s", url, this.info);
    }

    @Override
    protected void onError() {
        PubDto pub = this.getInfo();
        Util.error("PubInfoService::onError: %s %s", String.format(Constant.PUB_INFO_URL, pub.getPubId()), this.info);
    }
}