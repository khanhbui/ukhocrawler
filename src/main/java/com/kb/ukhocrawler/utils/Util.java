package com.kb.ukhocrawler.utils;

import java.io.File;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Util {
    public static Connection getConnection(String url) {
        return Jsoup.connect(url).userAgent(Constant.USER_AGENT);
    }

    public static String extract(String str, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);

        if (m.find()) {
           return m.group(2);
        } else {
           return "";
        }
    }

    public static void print(String msg, Object... args) {
        System.out.println(String.format("%s: %s", Calendar.getInstance().getTime(), String.format(msg, args)));
    }

    public static boolean createDirs(String path) {
        File file = new File(new File(path).getParent());
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static void error(String msg, Object... args) {
        System.err.println(String.format("%s: %s", Calendar.getInstance().getTime(), String.format(msg, args)));
    }
}
