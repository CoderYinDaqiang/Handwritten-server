package com.yun.server.utils;

import com.yun.server.catalina.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerXmlUtils {

    public static List<Service> getServices(){
        File file = Constant.serverXml;
        List<Service> serviceList = new ArrayList<>();
        try {
            Document document = Jsoup.parse(file, "utf-8");
            Elements elements = document.select("Service");
            for (Element element : elements) {
                String name = element.attr("name");
                Engine engine = getEngine(element);
                Service service = new Service(name, engine);
                List<Connector> connectorList = getConnectors(element, service);
                service.setConnectorList(connectorList);
                serviceList.add(service);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serviceList;
    }

    private static List<Connector> getConnectors(Element serviceElement, Service service) {
        List<Connector> connectorList = new ArrayList<>();
        Elements elements = serviceElement.select("Connector");
        for (Element element : elements) {
            String port = element.attr("port");
            Connector connector = new Connector(Integer.parseInt(port), service);
            connectorList.add(connector);
        }
        return connectorList;
    }

    private static Engine getEngine(Element serviceElement) throws FileNotFoundException {
        Elements element = serviceElement.select("Engine");
        String name = element.attr("name");
        String defaultHost = element.attr("defaultHost");
        List<Host> hostList = getHosts(element);
        Engine engine = new Engine(name, defaultHost, hostList);
        return engine;
    }

    private static List<Host> getHosts(Elements engineElement) throws FileNotFoundException {
        List<Host> hostList = new ArrayList<>();
        Elements elements = engineElement.select("Host");
        for (Element element : elements) {
            String name = element.attr("name");
            String appBase = element.attr("appBase");
            Host host = new Host(name, appBase);
            Map<String, Context> contextMap = getContexts(element, host);
            host.setContextMap(contextMap);
            hostList.add(host);
        }
        return hostList;
    }

    private static Map<String, Context> getContexts(Element hostElement, Host host) throws FileNotFoundException {
        HashMap<String, Context> contextMap = new HashMap<>();
        scanContextOfAppBase(host, contextMap);
        scanContextOfServerXml(hostElement, contextMap);
        return contextMap;
    }

    private static void scanContextOfServerXml(Element hostElement, HashMap<String, Context> contextMap) {
        Elements elements = hostElement.select("Context");
        for (Element element : elements) {
            String path = element.attr("path");
            String docBase = element.attr("docBase");
            Context context = new Context(path, docBase);
            contextMap.put(path, context);
        }
    }

    private static void scanContextOfAppBase(Host host, HashMap<String, Context> contextMap) throws FileNotFoundException {
        String appBase = host.getAppBase();
        File appBaseFile = new File(appBase);
        if(!appBaseFile.exists()){
            throw new FileNotFoundException("appBase[" + appBase + "]不存在");
        }
        File[] files = appBaseFile.listFiles();
        for (File file : files) {
            String path = file.getName();
            if(path.equals("ROOT")){
                path = "/";
            } else {
                path = "/" + path;
            }
            Context context = new Context(path, file.getAbsolutePath());
            contextMap.put(path, context);
        }
    }



}
