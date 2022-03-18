package com.yun.server.catalina;

import java.util.List;

public class Service {

    private String name;

    private List<Connector> connectorList;

    private Engine engine;

    public Service(String name, Engine engine) {
        this.name = name;
        this.engine = engine;
    }

    public Service(String name, List<Connector> connectorList, Engine engine) {
        this.name = name;
        this.connectorList = connectorList;
        this.engine = engine;
    }

    public void setConnectorList(List<Connector> connectorList) {
        this.connectorList = connectorList;
    }

    public void start() {
        for (Connector connector : this.connectorList) {
            new Thread(connector).start();
        }
    }

    public List<Connector> getConnectorList() {
        return connectorList;
    }

    public Engine getEngine() {
        return engine;
    }
}
