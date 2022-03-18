package com.yun.server.JunitTest;

import com.yun.server.utils.StringUtils;
import org.junit.Test;


public class StringUtilsTest {


    @Test
    public void testRemove(){
        String s = StringUtils.removePrefix("/as", "/as");
        System.out.println(s);
    }

    @Test
    public void testSubString(){
        String s = StringUtils.subString("/first/index.html", "/", "/");
        System.out.println(s);
    }
}
