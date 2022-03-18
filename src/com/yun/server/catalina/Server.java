package com.yun.server.catalina;

import com.yun.server.utils.ServerXmlUtils;

import java.util.List;

public class Server {

    private List<Service> serviceList;

    public Server() {
        this.serviceList = ServerXmlUtils.getServices();
    }

    public void start(){
        for (Service service : this.serviceList) {
            service.start();
        }
    }


}
