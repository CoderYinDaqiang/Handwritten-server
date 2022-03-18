package com.yun.server.JunitTest;

import com.yun.server.catalina.Context;
import com.yun.server.utils.WebXmlUtils;
import org.junit.Test;

public class WebXmlUtilsTest {


    @Test
    public void testMimeMap(){

        WebXmlUtils.getMimeType("/1.html");
    }

    @Test
    public void testSessionTimeOut(){
        Integer sessionTimeOut = WebXmlUtils.getSessionTimeOut();
        System.out.println(sessionTimeOut);
    }
}
