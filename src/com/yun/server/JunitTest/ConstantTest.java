package com.yun.server.JunitTest;

import com.yun.server.utils.Constant;
import org.junit.Test;

public class ConstantTest {

    @Test
    public void testConstant(){

        System.out.println(Constant.webapps);
        System.out.println(Constant.ROOT);
        String property = System.getProperty("user.dir");
        System.out.println(property);
    }
}
