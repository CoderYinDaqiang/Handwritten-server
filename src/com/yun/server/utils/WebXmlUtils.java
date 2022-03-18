package com.yun.server.utils;

import com.yun.server.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WebXmlUtils {

    private static List<String> welcomeFileList = new ArrayList<String>();

    private static Map<String, String> mimeMappingMap = new HashMap<>();

    private static Integer defaultTimeOut = null;

    static {
        parseWelcomeFile();
        parseMimeMapping();
        parseSessionConfig();
    }


    public static Integer getSessionTimeOut(){
        return defaultTimeOut;
    }


    private static void parseSessionConfig() {
        File file = Constant.webXml;
        try {
            Document document = Jsoup.parse(file, "utf-8");
            Elements element = document.select("session-config session-timeout");
            String text = element.text();
            defaultTimeOut = Integer.parseInt(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void parseWelcomeFile() {
        File file = Constant.webXml;
        try {
            Document document = Jsoup.parse(file, "utf-8");
            Elements elements = document.select("welcome-file-list welcome-file");
            for (Element element : elements) {
                String welcomeFileText = element.text();
                welcomeFileList.add(welcomeFileText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getWelcomeFile(Context context){
        for (String welcomeFile : welcomeFileList) {
            File file = new File(context.getDocBase() + "/" + welcomeFile);
            if(file.exists() && !file.isDirectory()){
                return welcomeFile;
            }
        }
        return "index.html";
    }


    private static void parseMimeMapping() {
        File file = Constant.webXml;
        try {
            Document document = Jsoup.parse(file, "utf-8");
            Elements elements = document.select("mime-mapping");
            // current file exist 3 mime-mapping, of which element is one ;
            for (Element element : elements) {
                Elements extension = element.select("extension");
                Elements mimeType = element.select("mime-type");
                String extensionText = extension.text();
                String mimeTypeText = mimeType.text();
                mimeMappingMap.put(extensionText, mimeTypeText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getMimeType(String servletPath) {
        Set<String> keySet = mimeMappingMap.keySet();
        for (String key : keySet) {
            if(servletPath.endsWith(key)){
                if(key.equals("html")){
                    return mimeMappingMap.get(key) + ";charset=utf-8";
                }
                return mimeMappingMap.get(key);
            }
        }
        return null;
    }
}
