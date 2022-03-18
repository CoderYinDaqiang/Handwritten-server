package com.yun.server.JunitTest;

import com.yun.server.catalina.Context;
import com.yun.server.catalina.Server;
import com.yun.server.catalina.Service;
import com.yun.server.utils.ServerXmlUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ServeXmlTest {


    @Test
    public void testGetContext(){
    }

    @Test
    public void testGetService(){
        List<Service> services = ServerXmlUtils.getServices();
    }
}
