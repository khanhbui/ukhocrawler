package com.kb.ukhocrawler.utils;

public class Constant {
    public static final String INDEX_URL = "http://www.ukho.gov.uk/onlinecatalogue/index.asp?UserType=commercial&SaveSettings=no&GraphicSet=full&ReferringURL=&Submit=Enter+site";
    public static final String SEARCH_URL = "http://www.ukho.gov.uk/onlinecatalogue/browse_SNCs_results.asp?FilterMethod=1";
    public static final String INFO_URL = "http://www.ukho.gov.uk/onlinecatalogue/popup_details_SNCs.asp?ChartID=%s";
    public static final String PREVIEW_URL = "http://www.ukho.gov.uk/onlinecatalogue/resources/includes/display_image.asp?ChartImageID=%s";

    public static final String USER_AGENT = "Mozilla";
    public static final String CHART_PREFIX = "chartPrefix";
    public static final String CHART_NUMBER = "ChartNumber";
    public static final String CHART_SUFFIX = "ChartSuffix";
    public static final int DEFAULT_CHUNK_SIZE = 56;
    public static final String INFO_PATH = "%s.txt";
    public static final String PREVIEW_PATH = "%s.jpg";
    public static final String GENERAL_INFO_PATH = "%sinfo.txt";

    public static final String SEARCH_BOOK_URL = "http://www.ukho.gov.uk/onlinecatalogue/browse_PUBs_results.asp?FilterMethod=1";
    public static final String PUB_NUMBER = "PubNumber";
    public static final String PUB_INFO_URL = "http://www.ukho.gov.uk/onlinecatalogue/popup_details_pubs.asp?PubID=%s";

    public static final String MAIN_URL = "http://www.ukho.gov.uk/onlinecatalogue/";

    public static final String VOA_URL = "http://learningenglish.voanews.com";
    public static final String VOA_AS_IT_IS_URL = "http://learningenglish.voanews.com/archive/as-it-is/%d/3521/3521.html?tab=2";
}
