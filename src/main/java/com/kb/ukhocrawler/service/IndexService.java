package com.kb.ukhocrawler.service;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;

import com.kb.ukhocrawler.utils.Constant;
import com.kb.ukhocrawler.utils.Util;

public class IndexService {

    private Connection connection;

    public void submit() throws IOException {
        Util.print("Fetching %s...", Constant.INDEX_URL);

        connection = Util.getConnection(Constant.INDEX_URL);
        connection.get();

        Util.print("Done %s.", Constant.INDEX_URL);
    }

    public Map<String, String> getCookies() {
        if (connection != null && connection.response() != null) {
            return connection.response().cookies();
        }
        return null;
    }
}
