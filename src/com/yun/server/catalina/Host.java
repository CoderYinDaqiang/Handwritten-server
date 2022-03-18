package com.yun.server.catalina;

import java.util.List;
import java.util.Map;

public class Host {

    private String name;

    private String appBase;

    //private List<Context> contextList;

    private Map<String, Context> contextMap;

    public Host(String name, String appBase) {
        this.name = name;
        this.appBase = appBase;
    }

    public String getAppBase() {
        return appBase;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, Context> contextMap) {
        this.contextMap = contextMap;
    }

    public String getName() {
        return name;
    }
}
