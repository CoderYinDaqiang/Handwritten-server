package com.yun.server.JunitTest;

import com.yun.server.utils.ContextUtils;
import org.junit.Test;

public class ContextXmlTest {

    @Test
    public void testWatchedResource(){
        String watchedResource = ContextUtils.getWatchedResource();
        System.out.println(watchedResource);
    }
}
