package com.yun.server.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class ContextUtils {

    public static String getWatchedResource(){
        File file = Constant.contextXml;
        String watchedResourceString = null;
        try {
            Document document = Jsoup.parse(file, "utf-8");
            Elements element = document.select("WatchedResource");
            String text = element.text();
            watchedResourceString = text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return watchedResourceString;
    }
}
