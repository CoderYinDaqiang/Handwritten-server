package com.yun.server.catalina;

import java.util.List;

public class Engine {

    private String name;

    private String defaultHost;

    private List<Host> hostList;

    public Engine(String name, String defaultHost, List<Host> hostList) {
        this.name = name;
        this.defaultHost = defaultHost;
        this.hostList = hostList;
    }

    public String getName() {
        return name;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public List<Host> getHostList() {
        return hostList;
    }
}
